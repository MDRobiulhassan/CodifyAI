package com.lovable.codifyAI.mapper;

import com.lovable.codifyAI.dto.auth.SignupRequest;
import com.lovable.codifyAI.dto.auth.UserProfileResponse;
import com.lovable.codifyAI.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(SignupRequest signupRequest);
    UserProfileResponse toUserProfileResponse(User user);
}
