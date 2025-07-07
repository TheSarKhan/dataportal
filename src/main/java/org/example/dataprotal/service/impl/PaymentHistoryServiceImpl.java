package org.example.dataprotal.service.impl;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dataprotal.enums.PaymentStatus;
import org.example.dataprotal.enums.PaymentType;
import org.example.dataprotal.enums.Subscription;
import org.example.dataprotal.exception.ResourceCanNotFoundException;
import org.example.dataprotal.model.user.PaymentHistory;
import org.example.dataprotal.model.user.User;
import org.example.dataprotal.repository.user.PaymentHistoryRepository;
import org.example.dataprotal.repository.user.UserRepository;
import org.example.dataprotal.service.PaymentHistoryService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentHistoryServiceImpl implements PaymentHistoryService {
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final UserRepository userRepository;

    @Override
    public List<PaymentHistory> getPaymentHistoryByUserEmail(String email) throws ResourceCanNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            log.error("Can not found the user {}", email);
            return new ResourceCanNotFoundException("Can not found the user {}" + email);
        });
        return paymentHistoryRepository.findPaymentHistoryByUserId(user.getId());
    }

    @Override
    public List<PaymentHistory> getAll() {
        return paymentHistoryRepository.findAll();
    }

    public List<PaymentHistory> filterPayments(String email, PaymentStatus status, PaymentType paymentType,
                                               Subscription subscription, LocalDate fromDate, LocalDate toDate,
                                               BigDecimal minAmount, BigDecimal maxAmount
    ) throws ResourceCanNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            log.error("Can not found the user {}", email);
            return new ResourceCanNotFoundException("Can not found the user {}" + email);
        });
        return paymentHistoryRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("user").get("id"), user.getId()));
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (paymentType != null) predicates.add(cb.equal(root.get("paymentType"), paymentType));
            if (subscription != null) predicates.add(cb.equal(root.get("subscription"), subscription));
            if (fromDate != null) predicates.add(cb.greaterThanOrEqualTo(root.get("date"), fromDate));
            if (toDate != null) predicates.add(cb.lessThanOrEqualTo(root.get("date"), toDate));
            if (minAmount != null) predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
            if (maxAmount != null) predicates.add(cb.lessThanOrEqualTo(root.get("amount"), maxAmount));
            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }
}
