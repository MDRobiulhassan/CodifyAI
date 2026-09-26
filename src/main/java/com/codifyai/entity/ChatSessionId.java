package com.codifyai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class ChatSessionId implements Serializable {

    @Column(name = "project_id")
    Long projectId;

    @Column(name = "user_id")
    Long userId;
}