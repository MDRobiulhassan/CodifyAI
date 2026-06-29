package com.lovable.codifyAI.entity;

import com.lovable.codifyAI.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "subscription")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    Plan plan;

    @Column(nullable = false, unique = true)
    String stripeSubscriptionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    SubscriptionStatus status;

    @Column(nullable = false)
    Instant currentPeriodStart;

    Boolean currentPeriodEnd;

    @CreationTimestamp
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;
}
