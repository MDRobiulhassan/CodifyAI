package com.lovable.codifyAI.dto.member;

import com.lovable.codifyAI.enums.ProjectRole;

import java.time.Instant;

public record MemberResponse(
        Long id,
        String username,
        String name,
        String avatarUrl,
        ProjectRole projectRole,
        Instant invitedAt
) {
}
