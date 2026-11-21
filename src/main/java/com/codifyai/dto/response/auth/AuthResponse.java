package com.codifyai.dto.response.auth;

public record AuthResponse(
        String token,
        UserProfileResponse user
) {
}
