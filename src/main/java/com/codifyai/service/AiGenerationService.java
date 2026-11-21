package com.codifyai.service;


import com.codifyai.dto.response.chat.StreamResponse;
import reactor.core.publisher.Flux;

public interface AiGenerationService {
    Flux<StreamResponse> streamResponse(String message, Long Long);
}
