package com.codifyai.service;

import com.codifyai.dto.response.auth.AuthResponse;
import com.codifyai.dto.request.auth.LoginRequest;
import com.codifyai.dto.request.auth.SignupRequest;

public interface AuthService {

    AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);
}
