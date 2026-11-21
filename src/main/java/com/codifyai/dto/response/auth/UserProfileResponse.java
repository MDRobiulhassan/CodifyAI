package com.codifyai.dto.response.auth;

public record UserProfileResponse(
        Long id,
        String username,
        String name
) {
}
