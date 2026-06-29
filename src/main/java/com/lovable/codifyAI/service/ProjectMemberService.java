package com.lovable.codifyAI.service;

import com.lovable.codifyAI.dto.member.InviteMemberRequest;
import com.lovable.codifyAI.dto.member.MemberResponse;
import com.lovable.codifyAI.dto.member.UpdateMemberRoleRequest;

import java.util.List;

public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(Long projectId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest request);

    MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request);

    void deleteProjectMember(Long projectId, Long memberId);
}
