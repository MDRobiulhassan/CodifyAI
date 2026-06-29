package com.lovable.codifyAI.serviceimpl;

import com.lovable.codifyAI.dto.member.InviteMemberRequest;
import com.lovable.codifyAI.dto.member.MemberResponse;
import com.lovable.codifyAI.dto.member.UpdateMemberRoleRequest;
import com.lovable.codifyAI.entity.Project;
import com.lovable.codifyAI.entity.ProjectMember;
import com.lovable.codifyAI.entity.ProjectMemberId;
import com.lovable.codifyAI.entity.User;
import com.lovable.codifyAI.mapper.ProjectMemberMapper;
import com.lovable.codifyAI.repository.ProjectMemberRepository;
import com.lovable.codifyAI.repository.ProjectRepository;
import com.lovable.codifyAI.repository.UserRepository;
import com.lovable.codifyAI.security.AuthUtil;
import com.lovable.codifyAI.security.SecurityExpressions;
import com.lovable.codifyAI.service.ProjectMemberService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProjectMemberServiceImpl implements ProjectMemberService {

    ProjectMemberRepository projectMemberRepository;
    ProjectRepository projectRepository;
    ProjectMemberMapper projectMemberMapper;
    UserRepository userRepository;
    AuthUtil authUtil;
    private final SecurityExpressions security;

    @Override
    @PreAuthorize("@security.canViewMembers(#projectId)")
    public List<MemberResponse> getProjectMembers(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAllAccessibleProjectByUser(userId, projectId);

        return new ArrayList<>(projectMemberRepository.findByIdProjectId(projectId)
                .stream()
                .map(projectMemberMapper::toProjectMemberResponseFromMember)
                .toList());
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse inviteMember(Long projectId, InviteMemberRequest request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAllAccessibleProjectByUser(userId, projectId);

        User invitee = userRepository.findByUsername(request.username()).orElseThrow();

        if (invitee.getId().equals(userId)) {
            throw new RuntimeException("Cannot invite Yourself");
        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, invitee.getId());

        if (projectMemberRepository.existsById(projectMemberId)) {
            throw new RuntimeException("Cannot invite Once Again");
        }

        ProjectMember member = ProjectMember.builder()
                .id(projectMemberId)
                .project(project)
                .user(invitee)
                .projectRole(request.role())
                .invitedAt(Instant.now())
                .build();

        projectMemberRepository.save(member);

        return projectMemberMapper.toProjectMemberResponseFromMember(member);

    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAllAccessibleProjectByUser(userId, projectId);

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId).orElseThrow();

        projectMember.setProjectRole(request.role());
        projectMemberRepository.save(projectMember);
        return projectMemberMapper.toProjectMemberResponseFromMember(projectMember);
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public void deleteProjectMember(Long projectId, Long memberId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAllAccessibleProjectByUser(userId, projectId);

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);

        if (!projectMemberRepository.existsById(projectMemberId)) {
            throw new RuntimeException("Member Not Found");
        }

        projectMemberRepository.deleteById(projectMemberId);
    }

    public Project getAllAccessibleProjectByUser(Long userId, Long projectId) {
        return projectRepository.findAccessibleProjectById(userId, projectId).orElseThrow();
    }
}
