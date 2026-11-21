package com.codifyai.service;

import com.codifyai.dto.response.project.FileContentResponse;
import com.codifyai.dto.response.project.FileTreeResponse;

public interface ProjectFileService {
    FileTreeResponse getFileTree(Long projectId);

    FileContentResponse getFileContent(Long projectId, String path);

    void saveFile(Long projectId, String filePath, String fileContent);
}
