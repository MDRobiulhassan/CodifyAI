package com.codifyai.service;

import com.codifyai.dto.project.ProjectRequest;
import com.codifyai.dto.project.ProjectResponse;
import com.codifyai.dto.project.ProjectSummaryResponse;

import java.util.List;

public interface ProjectService {
    List<ProjectSummaryResponse> getUserProjects();

    ProjectSummaryResponse getUserProjectById(Long id);

    ProjectResponse createProject(ProjectRequest request);

    ProjectResponse updateProject(ProjectRequest request, Long id);

    void softDelete(Long id);
}
