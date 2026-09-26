package com.codifyai.dto.chat;

import com.codifyai.entity.ChatEvent;
import com.codifyai.entity.ChatSession;
import com.codifyai.enums.MessageRole;

import java.time.Instant;
import java.util.List;

public record ChatResponse(

        Long id,
        ChatSession chatSession,
        MessageRole role,
        String content,
        List<ChatEvent> events,
        Integer tokenUsed,
        Instant createdAt
) {

}
