package com.lovable.codifyAI.dto.auth;

public record AuthResponse(
        String token,
        UserProfileResponse user
) {
}
