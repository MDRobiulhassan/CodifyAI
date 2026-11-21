package com.codifyai.service;

import com.codifyai.dto.request.project.ProjectRequest;
import com.codifyai.dto.response.project.ProjectResponse;
import com.codifyai.dto.response.project.ProjectSummaryResponse;

import java.util.List;

public interface ProjectService {
    List<ProjectSummaryResponse> getUserProjects();

    ProjectSummaryResponse getUserProjectById(Long id);

    ProjectResponse createProject(ProjectRequest request);

    ProjectResponse updateProject(ProjectRequest request, Long id);

    void softDelete(Long id);
}
