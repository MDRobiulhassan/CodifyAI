package com.lovable.codifyAI.serviceimpl;

import com.lovable.codifyAI.dto.auth.UserProfileResponse;
import com.lovable.codifyAI.repository.UserRepository;
import com.lovable.codifyAI.security.AuthUtil;
import com.lovable.codifyAI.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    UserRepository userRepository;
    AuthUtil authUtil;

    @Override
    public UserProfileResponse getProfile() {
        Long userId = authUtil.getCurrentUserId();
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}
