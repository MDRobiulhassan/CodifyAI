package com.lovable.codifyAI.dto.auth;

public record UserProfileResponse(
        Long id,
        String username,
        String name
) {
}
