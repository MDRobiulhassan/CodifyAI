package com.codifyai.serviceimpl;

import com.codifyai.dto.subscription.PlanResponse;
import com.codifyai.dto.subscription.SubscriptionResponse;
import com.codifyai.entity.UsageLog;
import com.codifyai.repository.UsageLogRepository;
import com.codifyai.security.AuthUtil;
import com.codifyai.service.SubscriptionService;
import com.codifyai.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class UsageServiceImpl implements UsageService {

    private final UsageLogRepository usageLogRepository;
    private final AuthUtil authUtil;
    private final SubscriptionService subscriptionService;

    @Override
    public void recordTokenUsage(Long userId, int actualTokens) {
        LocalDate today = LocalDate.now();
        UsageLog todayLog = usageLogRepository.findByUserIdAndDate(userId, today)
                .orElseGet(() -> createNewDailyUsageLog(userId, today));
        todayLog.setTokensUsed(todayLog.getTokensUsed() + actualTokens);
        usageLogRepository.save(todayLog);
    }

    @Override
    public void checkDailyTokensUsage() {
        Long userId = authUtil.getCurrentUserId();
        SubscriptionResponse subscriptionResponse = subscriptionService.getMySubscription();
        PlanResponse planResponse = subscriptionResponse.plan();

        LocalDate today = LocalDate.now();

        UsageLog todayLog = usageLogRepository.findByUserIdAndDate(userId, today)
                .orElseGet(() -> createNewDailyUsageLog(userId, today));

        int currentUsage = todayLog.getTokensUsed();
        int limit = planResponse.maxTokensPerDay();

        if(currentUsage >= limit){
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Daily token usage limit exceeded,Upgrade Now.");
        }
    }

    private UsageLog createNewDailyUsageLog(Long userId, LocalDate today) {
        UsageLog newLog = UsageLog.builder()
                .userId(userId)
                .date(today)
                .tokensUsed(0)
                .build();
        return usageLogRepository.save(newLog);
    }
}
