package org.example.dataprotal.mapper;

import org.example.dataprotal.dto.request.SubscriptionRequest;
import org.example.dataprotal.model.user.Subscription;

public class SubscriptionMapper {
    public static Subscription subscriptionRequestToSubscription(SubscriptionRequest request) {
        return Subscription.builder()
                .name(request.name())
                .advantages(request.advantages())
                .cureencyMonthlyAndYearlyPriceMap(request.cureencyMonthlyAndYearlyPriceMap())
                .build();
    }
}
