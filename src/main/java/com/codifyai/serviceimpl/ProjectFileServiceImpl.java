package com.codifyai.serviceimpl;

import com.codifyai.dto.project.FileContentResponse;
import com.codifyai.dto.project.FileNode;
import com.codifyai.entity.Project;
import com.codifyai.entity.ProjectFile;
import com.codifyai.error.ResourceNotFoundException;
import com.codifyai.mapper.ProjectFileMapper;
import com.codifyai.repository.ProjectFileRepository;
import com.codifyai.repository.ProjectRepository;
import com.codifyai.service.ProjectFileService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectFileServiceImpl implements ProjectFileService {

    private final MinioClient minioClient;
    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;
    private final ProjectFileMapper projectFileMapper;

    @Value("${minio.project-bucket}")
    private String projectBucket;

    private static final String BUCKET_NAME = "codify-ai";

    @Override
    public List<FileNode> getFileTree(Long projectId) {
        List<ProjectFile> projectFiles = projectFileRepository.findByProjectId(projectId);
        return projectFileMapper.toListOfFileNode(projectFiles);
    }

    @Override
    public FileContentResponse getFileContent(Long projectId, String path) {
        String objectName = projectId + "/" + path;
        try (
                InputStream is = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(BUCKET_NAME)
                                .object(objectName)
                                .build())) {

            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return new FileContentResponse(path, content);
        } catch (Exception e) {
            log.error("Failed to read file: {}/{}", projectId, path, e);
            throw new RuntimeException("Failed to read file content", e);
        }
    }

    @Override
    public void saveFile(Long projectId, String filePath, String fileContent) {
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ResourceNotFoundException("Project", projectId.toString())
        );

        String cleanPath = filePath.startsWith("/") ? filePath.substring(1) : filePath;
        String objectKey = project.getId() + "/" + cleanPath;

        try {
            byte[] contentBytes = fileContent.getBytes(StandardCharsets.UTF_8);
            InputStream inputStream = new ByteArrayInputStream(contentBytes);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(projectBucket)
                            .object(objectKey)
                            .stream(inputStream, (long) contentBytes.length, -1L)
                            .contentType(determineContentType(filePath))
                            .build()
            );

            ProjectFile projectFile = projectFileRepository.findByProjectIdAndPath(projectId, cleanPath)
                    .orElseGet(() -> ProjectFile.builder()
                            .project(project)
                            .path(cleanPath)
                            .minioObjectKey(objectKey)
                            .createdAt(Instant.now())
                            .build()
                    );

            projectFile.setUpdatedAt(Instant.now());
            projectFileRepository.save(projectFile);

            log.info("Saved File : {}", objectKey);
        } catch (Exception e) {
            log.error("Error Saving File : {}/{}", projectId, cleanPath, e);
            throw new RuntimeException("File Saved Failed", e);
        }
    }

    private String determineContentType(String filePath) {
        String type = URLConnection.guessContentTypeFromName(filePath);
        if (type != null) return type;
        if (filePath.endsWith(".jsx") || filePath.endsWith(".ts") || filePath.endsWith(".tsx"))
            return "text/javascript";
        if (filePath.endsWith(".json")) return "application/json";
        if (filePath.endsWith(".css")) return "text/css";
        return "text/plain";
    }
}
