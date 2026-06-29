package com.lovable.codifyAI.service;

import com.lovable.codifyAI.dto.subscription.*;

import java.util.List;

public interface SubscriptionService {
    List<PlanResponse> getAllActivePlans();

    SubscriptionReponse getMySubscription(Long userId);
}
