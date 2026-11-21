package com.codifyai.mapper;

import com.codifyai.dto.response.project.ProjectResponse;
import com.codifyai.dto.response.project.ProjectSummaryResponse;
import com.codifyai.entity.Project;
import com.codifyai.enums.ProjectRole;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectResponse toProjectResponse(Project project);
    List<ProjectSummaryResponse> toProjectSummaryResponse(List<Project> projects);
    ProjectSummaryResponse toProjectSummaryResponse(Project project, ProjectRole role);
}
