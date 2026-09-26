package com.codifyai.serviceimpl;

import com.codifyai.dto.chat.ChatResponse;
import com.codifyai.entity.ChatMessage;
import com.codifyai.entity.ChatSession;
import com.codifyai.entity.ChatSessionId;
import com.codifyai.mapper.ChatMapper;
import com.codifyai.repository.ChatMessageRepository;
import com.codifyai.repository.ChatSessionRepository;
import com.codifyai.security.AuthUtil;
import com.codifyai.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final AuthUtil authUtil;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMapper chatMapper;

    @Override
    public List<ChatResponse> getProjectChatHistory(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        ChatSession chatSession = chatSessionRepository.getReferenceById(
                new ChatSessionId(projectId, userId)
        );
        List<ChatMessage> chatMessageList = chatMessageRepository.findByChatSession(chatSession);
        return chatMapper.fromListOfChatMessage(chatMessageList);
    }
}
