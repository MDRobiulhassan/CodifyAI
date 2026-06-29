package com.lovable.codifyAI.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "plan")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String name;

    @Column(nullable = false, unique = true)
    String stripePriceId;

    @Column(nullable = false)
    Integer maxProjects;

    @Column(nullable = false)
    Integer maxTokensPerDay;

    @Column(nullable = false)
    Integer maxPreviews;

    @Column(nullable = false)
    Boolean unlimitedAi;

    @Column(nullable = false)
    Boolean active;
}
