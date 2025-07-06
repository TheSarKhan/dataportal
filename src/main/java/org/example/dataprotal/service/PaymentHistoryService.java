package org.example.dataprotal.service;

import org.example.dataprotal.enums.PaymentStatus;
import org.example.dataprotal.enums.PaymentType;
import org.example.dataprotal.enums.Subscription;
import org.example.dataprotal.exception.ResourceCanNotFoundException;
import org.example.dataprotal.model.paymenthistory.PaymentHistory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PaymentHistoryService {
    List<PaymentHistory> getPaymentHistoryByUserEmail(String email) throws ResourceCanNotFoundException;
    List<PaymentHistory> getAll();
    List<PaymentHistory> filterPayments(String email, PaymentStatus status, PaymentType paymentType,
                                               Subscription subscription, LocalDate fromDate, LocalDate toDate,
                                               BigDecimal minAmount, BigDecimal maxAmount
    ) throws ResourceCanNotFoundException;
}
