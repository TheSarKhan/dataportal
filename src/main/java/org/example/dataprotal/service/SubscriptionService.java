package org.example.dataprotal.service;

import org.example.dataprotal.dto.request.SubscriptionRequest;
import org.example.dataprotal.model.user.Subscription;

import java.util.List;

public interface SubscriptionService {
    Subscription createSubscription(SubscriptionRequest request);

    List<Subscription> getAllSubscriptions();

    Subscription getSubscriptionById(Long id);

    Subscription updateSubscription(Long id, SubscriptionRequest request);

    void deleteSubscriptionById(Long id);
}
