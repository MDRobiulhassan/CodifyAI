package com.codifyai.mapper;

import com.codifyai.dto.subscription.PlanResponse;
import com.codifyai.dto.subscription.SubscriptionResponse;
import com.codifyai.entity.Plan;
import com.codifyai.entity.Subscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    SubscriptionResponse toSubscriptionResponse(Subscription subscription);
    PlanResponse toPlanResponse(Plan plan);
}
