package com.lovable.codifyAI.service;

import com.lovable.codifyAI.dto.subscription.PlanLimitResponse;
import com.lovable.codifyAI.dto.subscription.UsageTodayResponse;

public interface UsageService {
    UsageTodayResponse getTodayUsage(Long userId);

    PlanLimitResponse getCurrentSubscriptionLimitOfUser(Long userId);
}
