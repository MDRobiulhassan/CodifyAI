package com.codifyai.dto.response.subscription;

public record UsageTodayResponse(
        Integer tokenUsed,
        Integer tokenLimits,
        Integer previewsRunning,
        Integer previewsLimit
) {
}
