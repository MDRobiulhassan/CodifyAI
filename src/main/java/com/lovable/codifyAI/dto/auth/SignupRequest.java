package com.lovable.codifyAI.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @Email @NotBlank String username,
        @NotBlank @Size(max = 30,min = 1) String name,
        @Size(min = 6,max = 50) String password
) {
}
