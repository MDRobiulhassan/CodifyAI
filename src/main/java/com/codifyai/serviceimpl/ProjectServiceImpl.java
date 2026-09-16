package com.codifyai.serviceimpl;

import com.codifyai.dto.project.ProjectRequest;
import com.codifyai.dto.project.ProjectResponse;
import com.codifyai.dto.project.ProjectSummaryResponse;
import com.codifyai.entity.Project;
import com.codifyai.entity.ProjectMember;
import com.codifyai.entity.ProjectMemberId;
import com.codifyai.entity.User;
import com.codifyai.enums.ProjectRole;
import com.codifyai.error.BadRequestException;
import com.codifyai.error.ResourceNotFoundException;
import com.codifyai.mapper.ProjectMapper;
import com.codifyai.repository.ProjectMemberRepository;
import com.codifyai.repository.ProjectRepository;
import com.codifyai.repository.UserRepository;
import com.codifyai.security.AuthUtil;
import com.codifyai.service.ProjectService;
import com.codifyai.service.ProjectTemplateService;
import com.codifyai.service.SubscriptionService;
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
    SubscriptionService subscriptionService;
    ProjectTemplateService projectTemplateService;


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

        if (!subscriptionService.CanCreateNewProject()) {
            throw new BadRequestException("You have reached the maximum number of projects allowed for your subscription plan. Please upgrade your plan to create more projects.");
        }

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
        projectTemplateService.initializeProjectFromTemplate(project.getId());
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
