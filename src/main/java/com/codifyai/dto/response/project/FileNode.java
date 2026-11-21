package com.codifyai.dto.response.project;

import org.jetbrains.annotations.NotNull;

public record FileNode(
        String path
) {
    @NotNull
    @Override
    public String toString() {
        return path;
    }
}
