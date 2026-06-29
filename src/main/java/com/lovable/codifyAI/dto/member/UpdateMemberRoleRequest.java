package com.lovable.codifyAI.dto.member;

import com.lovable.codifyAI.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest(
        @NotNull ProjectRole role
) {
}
