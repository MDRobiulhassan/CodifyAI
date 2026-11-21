package com.codifyai.dto.request.chat;

import com.codifyai.dto.response.chat.ChatEventResponse;
import com.codifyai.enums.MessageRole;

import java.time.Instant;
import java.util.List;

public record ChatResponse(
        Long id,
        MessageRole role,
        String content,
        List<ChatEventResponse> events,
        Integer tokenUsed,
        Instant createdAt
) {

}
