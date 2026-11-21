package com.codifyai.dto.response.chat;

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
