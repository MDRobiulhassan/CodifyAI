package com.lovable.codifyAI.controller;

import com.lovable.codifyAI.dto.member.InviteMemberRequest;
import com.lovable.codifyAI.dto.member.UpdateMemberRoleRequest;
import com.lovable.codifyAI.service.ProjectMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/members")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @GetMapping
    public ResponseEntity<?> getProjectMembers(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectMemberService.getProjectMembers(projectId));
    }

    @PostMapping
    public ResponseEntity<?> inviteMember(@PathVariable Long projectId, @Valid @RequestBody InviteMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectMemberService.inviteMember(projectId, request));
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<?> updateMemberRole(@PathVariable Long projectId, @PathVariable Long memberId, @Valid @RequestBody UpdateMemberRoleRequest request) {
        return ResponseEntity.ok(projectMemberService.updateMemberRole(projectId, memberId, request));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<?> deleteMember(@PathVariable Long projectId, @PathVariable Long memberId) {
        projectMemberService.deleteProjectMember(projectId, memberId);
        return ResponseEntity.noContent().build();
    }
}
