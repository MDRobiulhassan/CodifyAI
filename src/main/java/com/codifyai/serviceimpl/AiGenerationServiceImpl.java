package com.codifyai.serviceimpl;

import com.codifyai.dto.chat.StreamResponse;
import com.codifyai.entity.*;
import com.codifyai.enums.ChatEventType;
import com.codifyai.enums.MessageRole;
import com.codifyai.error.ResourceNotFoundException;
import com.codifyai.llm.LLMResponseParser;
import com.codifyai.llm.PromptUtils;
import com.codifyai.llm.advisors.FileTreeContextAdvisors;
import com.codifyai.llm.tools.CodeGenerationTools;
import com.codifyai.repository.*;
import com.codifyai.security.AuthUtil;
import com.codifyai.service.AiGenerationService;
import com.codifyai.service.ProjectFileService;
import com.codifyai.service.UsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;


@Service
@Slf4j
@RequiredArgsConstructor
public class AiGenerationServiceImpl implements AiGenerationService {

    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">([\\s\\S]*?)</file>", Pattern.DOTALL);
    private final ChatClient chatClient;
    private final AuthUtil authUtil;
    private final ProjectFileService projectFileService;
    private final FileTreeContextAdvisors fileTreeContextAdvisors;
    private final LLMResponseParser llmResponseParser;
    private final ChatSessionRepository chatSessionRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatEventRepository chatEventRepository;
    private final UsageService usageService;

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<StreamResponse> streamResponse(String userMessage, Long projectId) {
//        usageService.checkDailyTokensUsage();

        Long userId = authUtil.getCurrentUserId();
        ChatSession chatSession = createChatSessionIfNotExists(projectId, userId);

        Map<String, Object> advisorParams = Map.of(
                "userId", userId,
                "projectId", projectId
        );

        StringBuilder fullResponseBuffer = new StringBuilder();

        CodeGenerationTools codeGenerationTools = new CodeGenerationTools(projectFileService, projectId);

        AtomicReference<Long> startTime = new AtomicReference<>(System.currentTimeMillis());
        AtomicReference<Long> endTime = new AtomicReference<>(0L);
        AtomicReference<Usage> usageRef = new AtomicReference<>();

        return chatClient.prompt()
                .system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT)
                .user(userMessage)
                .tools(codeGenerationTools)
                .advisors(
                        advisorSpec -> advisorSpec
                                .params(advisorParams)
                                .advisors(fileTreeContextAdvisors)
                )
                .stream()
                .chatResponse()
                .doOnNext(response -> {
                    String content = Objects.requireNonNull(response.getResult()).getOutput().getText();

                    if (content != null && !content.isEmpty() && endTime.get() == 0) {
                        endTime.set(System.currentTimeMillis());
                    }

                    if (response.getMetadata().getUsage() != null) {
                        usageRef.set(response.getMetadata().getUsage());
                    }

                    fullResponseBuffer.append(content);
                })
                .doFinally(signalType -> Schedulers.boundedElastic()
                        .schedule(() ->
                                {
                                    long duration = (endTime.get() - startTime.get()) / 1000;
                                    finaliseChats(userMessage, chatSession, fullResponseBuffer.toString(), duration, usageRef.get());
                                }
                        ))
                .doOnError(error ->
                        log.error("Error while streaming chat response: {}", error.getMessage(), error)
                )
                .map(response -> {
                    String text = Objects.requireNonNull(response.getResult()).getOutput().getText();
                    return new StreamResponse(text != null ? text : "");
                })
                .onErrorResume(error -> Flux.just(
                        new StreamResponse(
                                "\n\n[Generation interrupted: " + error.getMessage() + "]"
                        )
                ));
    }

    private void finaliseChats(String userMessage, ChatSession chatSession, String fullText, Long duration, Usage usage) {
        Long projectId = chatSession.getProject().getId();

        if (usage != null) {
            int totalTokensUsed = usage.getTotalTokens();
            usageService.recordTokenUsage(chatSession.getUser().getId(), totalTokensUsed);
        }

        chatMessageRepository.save(
                ChatMessage.builder()
                        .chatSession(chatSession)
                        .role(MessageRole.USER)
                        .content(userMessage)
                        .tokenUsed(usage.getPromptTokens())
                        .build()
        );

        ChatMessage assistantChatMessage = ChatMessage.builder()
                .chatSession(chatSession)
                .role(MessageRole.ASSISTANT)
                .content("Assistant message here.....")
                .tokenUsed(usage.getCompletionTokens())
                .build();

        assistantChatMessage = chatMessageRepository.save(assistantChatMessage);

        List<ChatEvent> chatEventList = llmResponseParser.parseChatEvents(fullText, assistantChatMessage);
        chatEventList.add(0, ChatEvent.builder()
                .type(ChatEventType.THOUGHT)
                .chatMessage(assistantChatMessage)
                .content("Thought for " + duration + "s")
                .sequenceOrder(0)
                .build());

        chatEventList.stream()
                .filter(e -> e.getType() == ChatEventType.FILE_EDIT)
                .forEach(e -> projectFileService.saveFile(projectId, e.getFilePath(), e.getContent()));
        chatEventRepository.saveAll(chatEventList);

    }

    private ChatSession createChatSessionIfNotExists(Long projectId, Long userId) {
        ChatSessionId chatSessionId = new ChatSessionId(projectId, userId);
        ChatSession chatSession = chatSessionRepository.findById(chatSessionId).orElse(null);

        if (chatSession == null) {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

            chatSession = ChatSession.builder()
                    .id(chatSessionId)
                    .project(project)
                    .user(user)
                    .build();

            chatSession = chatSessionRepository.save(chatSession);
        }

        return chatSession;
    }
}
