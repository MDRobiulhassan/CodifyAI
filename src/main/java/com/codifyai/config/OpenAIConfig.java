package com.codifyai.config;

import com.openai.client.OpenAIClientAsync;
import com.openai.client.okhttp.OpenAIOkHttpClientAsync;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenAIConfig {

    @Bean
    public OpenAIClientAsync openAIClientAsync(
            @Value("${spring.ai.openai.api-key}") String apiKey
    ) {
        return OpenAIOkHttpClientAsync.builder()
                .apiKey(apiKey)
                .baseUrl("https://openrouter.ai/api/v1")
                .timeout(Duration.ofMinutes(5))
                .maxRetries(2)
                .build();
    }
}