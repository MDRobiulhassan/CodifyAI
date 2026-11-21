package com.codifyai.mapper;

import com.codifyai.dto.response.chat.ChatEventResponse;
import com.codifyai.entity.ChatMessage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    List<ChatEventResponse.ChatResponse> fromListOfChatMessage(List<ChatMessage> chatMessages);
}
