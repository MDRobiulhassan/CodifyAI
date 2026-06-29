package com.lovable.codifyAI.serviceimpl;

import com.lovable.codifyAI.dto.project.ProjectRequest;
import com.lovable.codifyAI.dto.project.ProjectResponse;
import com.lovable.codifyAI.dto.project.ProjectSummaryResponse;
import com.lovable.codifyAI.entity.Project;
import com.lovable.codifyAI.entity.ProjectMember;
import com.lovable.codifyAI.entity.ProjectMemberId;
import com.lovable.codifyAI.entity.User;
import com.lovable.codifyAI.enums.ProjectRole;
import com.lovable.codifyAI.error.ResourceNotFoundException;
import com.lovable.codifyAI.mapper.ProjectMapper;
import com.lovable.codifyAI.repository.ProjectMemberRepository;
import com.lovable.codifyAI.repository.ProjectRepository;
import com.lovable.codifyAI.repository.UserRepository;
import com.lovable.codifyAI.security.AuthUtil;
import com.lovable.codifyAI.service.ProjectService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;
    UserRepository userRepository;
    ProjectMapper projectMapper;
    ProjectMemberRepository projectMemberRepository;
    AuthUtil authUtil;

    @Override
    public List<ProjectSummaryResponse> getUserProjects() {
        Long userId = authUtil.getCurrentUserId();
        var projects = projectRepository.findAllAccessibleByUser(userId);
        return projectMapper.toProjectSummaryResponse(projects);
    }

    @Override
    @PreAuthorize("@security.canViewProject(#projectId)")
    public ProjectResponse getProjectById(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAllAccessibleProjectByUser(userId, projectId);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse createProject(ProjectRequest request) {
        Long userId = authUtil.getCurrentUserId();
//        User owner = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

        User owner = userRepository.getReferenceById(userId);

        Project project = Project.builder()
                .name(request.name())
                .isPublic(false)
                .build();
        project = projectRepository.save(project);

        ProjectMemberId projectMemberId = new ProjectMemberId(project.getId(), userId);
        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .projectRole(ProjectRole.OWNER)
                .user(owner)
                .acceptedAt(Instant.now())
                .invitedAt(Instant.now())
                .project(project)
                .build();

        projectMemberRepository.save(projectMember);

        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public ProjectResponse updateProject(ProjectRequest request, Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAllAccessibleProjectByUser(userId, projectId);

        project.setName(request.name());
        project = projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canDeleteProject(#projectId)")
    public void softDelete(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAllAccessibleProjectByUser(userId, projectId);

        project.setDeletedAt(Instant.now());
        projectRepository.save(project);
    }

    public Project getAllAccessibleProjectByUser(Long userId, Long projectId) {
        return projectRepository.findAccessibleProjectById(userId, projectId).orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));
    }
}
