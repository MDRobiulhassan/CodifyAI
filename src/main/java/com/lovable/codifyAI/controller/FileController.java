package com.lovable.codifyAI.controller;

import com.lovable.codifyAI.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/files")
public class FileController {

    private final FileService fileService;

    @GetMapping
    public ResponseEntity<?> getFileTree(@PathVariable Long projectId) {
        Long userId = 1L;
        return ResponseEntity.ok(fileService.getFileTree(projectId, userId));
    }

    @GetMapping("/{path}")
    public ResponseEntity<?> getFile(
            @PathVariable Long projectId, String path) {
        Long userId = 1L;
        return ResponseEntity.ok(fileService.getFileContent(projectId, userId, path));
    }

}
