package org.example.dataprotal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dataprotal.dto.request.SubscriptionRequest;
import org.example.dataprotal.model.user.Subscription;
import org.example.dataprotal.repository.user.SubscriptionRepository;
import org.example.dataprotal.service.SubscriptionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

import static org.example.dataprotal.mapper.SubscriptionMapper.subscriptionRequestToSubscription;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository repository;

    @Override
    public Subscription createSubscription(SubscriptionRequest request) {
        log.info("Create subscription : {}", request);
        return repository.save(subscriptionRequestToSubscription(request));
    }

    @Override
    public List<Subscription> getAllSubscriptions() {
        log.info("Get all subscriptions");
        return repository.findAll();
    }

    @Override
    public Subscription getSubscriptionById(Long id) {
        log.info("Get subscription by id : {}", id);
        return getSubscription(id);
    }

    @Override
    public Subscription updateSubscription(Long id, SubscriptionRequest request) {
        log.info("Update subscription by id : {}", id);
        Subscription subscription = getSubscription(id);
        Subscription updateSubscription = subscriptionRequestToSubscription(request);
        updateSubscription.setId(subscription.getId());
        return repository.save(updateSubscription);
    }

    @Override
    public void deleteSubscriptionById(Long id) {
        log.info("Delete subscription by id : {}", id);
        repository.deleteById(id);
    }

    private Subscription getSubscription(Long id) {
        return repository.findById(id).orElseThrow(() -> {
            log.error("Subscription not found with id : {}", id);
            return new NoSuchElementException("Subscription not found with id : " + id);
        });
    }
}
