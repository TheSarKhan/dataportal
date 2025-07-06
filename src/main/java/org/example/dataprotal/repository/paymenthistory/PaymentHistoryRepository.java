package org.example.dataprotal.repository.paymenthistory;

import org.example.dataprotal.model.paymenthistory.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory,Long>, JpaSpecificationExecutor<PaymentHistory> {

    List<PaymentHistory> findPaymentHistoryByUserId(Long userId);
}
