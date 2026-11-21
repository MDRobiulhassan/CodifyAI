package com.codifyai.dto.response.chat;

import com.codifyai.enums.ChatEventType;
import com.codifyai.enums.MessageRole;

import java.time.Instant;
import java.util.List;

public record ChatEventResponse(
        Long id,
        ChatEventType type,
        Integer sequenceOrder,
        String content,
        String filePath,
        String metaData
) {
    public static record ChatResponse(
            Long id,
            MessageRole role,
            String content,
            List<ChatEventResponse> events,
            Integer tokenUsed,
            Instant createdAt
    ) {

    }
}
