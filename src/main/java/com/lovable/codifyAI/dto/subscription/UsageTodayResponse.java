package com.lovable.codifyAI.dto.subscription;

public record UsageTodayResponse(
        Integer tokenUsed,
        Integer tokenLimits,
        Integer previewsRunning,
        Integer previewsLimit
) {
}
