package com.lovable.codifyAI.serviceimpl;

import com.lovable.codifyAI.dto.subscription.PlanLimitResponse;
import com.lovable.codifyAI.dto.subscription.UsageTodayResponse;
import com.lovable.codifyAI.service.UsageService;
import org.springframework.stereotype.Service;

@Service
public class UsageServiceImpl implements UsageService {

    @Override
    public UsageTodayResponse getTodayUsage(Long userId) {
        return null;
    }

    @Override
    public PlanLimitResponse getCurrentSubscriptionLimitOfUser(Long userId) {
        return null;
    }
}
