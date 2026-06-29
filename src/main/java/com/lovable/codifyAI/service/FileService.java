package com.lovable.codifyAI.service;

import com.lovable.codifyAI.dto.project.FileContentResponse;
import com.lovable.codifyAI.dto.project.FileNode;

import java.util.List;

public interface FileService {
    List<FileNode> getFileTree(Long projectId, Long userId);

    FileContentResponse getFileContent(Long projectId, Long userId, String path);
}
