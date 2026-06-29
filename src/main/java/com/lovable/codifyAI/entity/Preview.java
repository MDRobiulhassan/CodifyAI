package com.lovable.codifyAI.entity;

import com.lovable.codifyAI.enums.PreviewStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "preview")
public class Preview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    Project project;

    @Column(nullable = false)
    String nameSpace;

    @Column(nullable = false)
    String podName;

    @Column(nullable = false)
    String previewUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PreviewStatus status;


    Instant startedAt;
    Instant terminatedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    Instant createdAt;
}
