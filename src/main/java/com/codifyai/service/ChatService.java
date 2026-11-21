package com.codifyai.service;


import com.codifyai.dto.response.chat.ChatEventResponse;

import java.util.List;

public interface ChatService {
    List<ChatEventResponse.ChatResponse> getProjectChatHistory(Long projectId);
}
