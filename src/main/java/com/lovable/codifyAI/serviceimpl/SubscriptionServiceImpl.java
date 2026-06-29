package com.lovable.codifyAI.serviceimpl;

import com.lovable.codifyAI.dto.subscription.*;
import com.lovable.codifyAI.service.SubscriptionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Override
    public List<PlanResponse> getAllActivePlans() {
        return List.of();
    }

    @Override
    public SubscriptionReponse getMySubscription(Long userId) {
        return null;
    }
}
