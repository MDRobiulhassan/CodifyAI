package com.lovable.codifyAI.dto.subscription;

import java.time.Instant;

public record SubscriptionReponse(
        PlanResponse plan,
        String status,
        Instant periodEnd,
        Long tokensUsedThisCycle
) {
}
