package com.codifyai.service;

import com.codifyai.dto.response.auth.UserProfileResponse;

public interface UserService {
    UserProfileResponse getProfile();
}
