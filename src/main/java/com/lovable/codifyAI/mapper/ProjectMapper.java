package com.lovable.codifyAI.mapper;

import com.lovable.codifyAI.dto.project.ProjectResponse;
import com.lovable.codifyAI.dto.project.ProjectSummaryResponse;
import com.lovable.codifyAI.entity.Project;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectResponse toProjectResponse(Project project);
    List<ProjectSummaryResponse> toProjectSummaryResponse(List<Project> projects);

}
