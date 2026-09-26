package com.codifyai.mapper;

import com.codifyai.dto.chat.ChatResponse;
import com.codifyai.entity.ChatMessage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    List<ChatResponse> fromListOfChatMessage(List<ChatMessage> chatMessages);
}
