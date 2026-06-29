package com.lovable.codifyAI.entity;

import com.lovable.codifyAI.enums.ProjectRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "project_member")
public class ProjectMember {

    @EmbeddedId
    ProjectMemberId id;

    @ManyToOne
    @MapsId("projectId")
    @JoinColumn(name = "project_id", nullable = false)
    Project project;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ProjectRole projectRole;

//    @ManyToOne
//    @JoinColumn(name = "invited_by")
//    User invitedBy;

    @Column(nullable = false, updatable = false)
    Instant invitedAt;

    Instant acceptedAt;
}
