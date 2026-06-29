package com.lovable.codifyAI.dto.project;

import java.time.Instant;

public record FileNode(
        String path,
        Instant modifiedAt,
        Long id,
        String type
) {
}
