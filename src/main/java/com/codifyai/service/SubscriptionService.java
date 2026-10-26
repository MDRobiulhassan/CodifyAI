package com.codifyai.service;

import com.codifyai.dto.subscription.PlanResponse;
import com.codifyai.dto.subscription.SubscriptionResponse;
import com.codifyai.enums.SubscriptionStatus;

import java.time.Instant;
import java.util.List;

public interface SubscriptionService {
    List<PlanResponse> getAllActivePlans();

    SubscriptionResponse getMySubscription();

    //    void activateSubscription(Long userId, Long planId, String subscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd);
    void activateSubscription(Long userId, Long planId, String subscriptionId,
                              SubscriptionStatus status, Instant periodStart, Instant periodEnd);

    void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId);

    void cancelSubscription(String gatewaySubscriptionId);

    void renewSubscriptionPeriod(String subscriptionId, Instant periodStart, Instant periodEnd);

    void markSubscriptionPastDue(String subId);

    boolean CanCreateNewProject();
}
