package org.example.dataprotal.service;

import org.example.dataprotal.dto.response.PaymentHistoryResponse;
import org.example.dataprotal.enums.PaymentStatus;
import org.example.dataprotal.enums.PaymentType;
import org.example.dataprotal.model.user.PaymentHistory;
import org.example.dataprotal.model.user.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PaymentHistoryService {
    PaymentHistory save(PaymentHistory paymentHistory);

    PaymentHistoryResponse getPaymentHistoryByUser(User user);

    List<PaymentHistory> getAll();

    List<PaymentHistory> filterPayments(User user,
                                        PaymentStatus status,
                                        PaymentType paymentType,
                                        Long subscriptionId,
                                        LocalDate fromDate,
                                        LocalDate toDate,
                                        BigDecimal minAmount,
                                        BigDecimal maxAmount);
}
