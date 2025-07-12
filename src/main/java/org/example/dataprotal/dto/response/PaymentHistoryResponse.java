package org.example.dataprotal.dto.response;

import org.example.dataprotal.model.user.PaymentHistory;

import java.time.LocalDateTime;
import java.util.List;

public record PaymentHistoryResponse(List<PaymentHistory> paymentHistories,
                                     LocalDateTime nextPaymentDate) {
}
