package com.lovable.codifyAI.service;

import com.lovable.codifyAI.dto.project.ProjectRequest;
import com.lovable.codifyAI.dto.project.ProjectResponse;
import com.lovable.codifyAI.dto.project.ProjectSummaryResponse;

import java.util.List;

public interface ProjectService {
    List<ProjectSummaryResponse> getUserProjects();

    ProjectResponse getProjectById(Long id);

    ProjectResponse createProject(ProjectRequest request);

    ProjectResponse updateProject(ProjectRequest request, Long id);

    void softDelete(Long id);
}
