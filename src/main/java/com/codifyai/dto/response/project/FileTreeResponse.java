package com.codifyai.dto.response.project;

import java.util.List;

public record FileTreeResponse(
        List<FileNode> files
) {
}
