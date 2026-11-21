package com.codifyai.service;

import com.codifyai.dto.request.member.InviteMemberRequest;
import com.codifyai.dto.response.member.MemberResponse;
import com.codifyai.dto.request.member.UpdateMemberRoleRequest;

import java.util.List;

public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(Long projectId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest request);

    MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request);

    void deleteProjectMember(Long projectId, Long memberId);
}
