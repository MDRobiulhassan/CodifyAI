package com.codifyai.dto.chat;

import com.codifyai.entity.ChatMessage;
import com.codifyai.enums.ChatEventType;

public record ChatEventResponse(
        Long id,
        ChatEventType type,
        Integer sequenceOrder,
        String content,
        String filePath,
        String metaData
) {
}
