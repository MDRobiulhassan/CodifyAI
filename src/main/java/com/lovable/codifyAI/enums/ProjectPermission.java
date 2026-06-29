package com.lovable.codifyAI.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProjectPermission {
    EDIT("project:edit"),
    VIEW("project:view"),
    DELETE("project:delete"),

    MANAGE_MEMBERS("project_members:manage"),
    VIEW_MEMBERS("project_members:view");

    private final String value;
}
