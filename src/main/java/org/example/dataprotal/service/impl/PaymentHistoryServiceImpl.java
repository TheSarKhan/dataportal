package org.example.dataprotal.service.impl;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dataprotal.dto.response.PaymentHistoryResponse;
import org.example.dataprotal.enums.PaymentStatus;
import org.example.dataprotal.enums.PaymentType;
import org.example.dataprotal.model.user.PaymentHistory;
import org.example.dataprotal.model.user.User;
import org.example.dataprotal.repository.user.PaymentHistoryRepository;
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

    @Override
    public PaymentHistory save(PaymentHistory paymentHistory) {
        return paymentHistoryRepository.save(paymentHistory);
    }

    @Override
    public PaymentHistoryResponse getPaymentHistoryByUser(User user) {
        return new PaymentHistoryResponse(paymentHistoryRepository.findPaymentHistoryByUserId(user.getId()),
                user.getNextPaymentTime());
    }

    @Override
    public List<PaymentHistory> getAll() {
        return paymentHistoryRepository.findAll();
    }

    @Override
    public List<PaymentHistory> filterPayments(User user, PaymentStatus status, PaymentType paymentType,
                                               Long subscriptionId, LocalDate fromDate, LocalDate toDate,
                                               BigDecimal minAmount, BigDecimal maxAmount) {
        return paymentHistoryRepository.findAll(
                (root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.equal(root.get("userId"), user.getId()));
                    if (status != null) predicates.add(cb.equal(root.get("status"), status));
                    if (paymentType != null) predicates.add(cb.equal(root.get("paymentType"), paymentType));
                    if (subscriptionId != null) predicates.add(cb.equal(root.get("subscriptionId"), subscriptionId));
                    if (fromDate != null) predicates.add(cb.greaterThanOrEqualTo(root.get("date"), fromDate));
                    if (toDate != null) predicates.add(cb.lessThanOrEqualTo(root.get("date"), toDate));
                    if (minAmount != null) predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
                    if (maxAmount != null) predicates.add(cb.lessThanOrEqualTo(root.get("amount"), maxAmount));
                    return cb.and(predicates.toArray(new Predicate[0]));
                });
    }
}
