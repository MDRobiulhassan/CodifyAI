package com.lovable.codifyAI.service;

import com.lovable.codifyAI.dto.auth.AuthResponse;
import com.lovable.codifyAI.dto.auth.LoginRequest;
import com.lovable.codifyAI.dto.auth.SignupRequest;

public interface AuthService {

    AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);
}
