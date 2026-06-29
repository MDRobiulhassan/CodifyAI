package com.lovable.codifyAI.dto.subscription;

public record PlanResponse(
        Long id,
        String name,
        String stripePriceId,
        Integer maxProjects,
        Integer maxTokensPerDay,
        Boolean unlimitedAi,
        String price
) {
}
