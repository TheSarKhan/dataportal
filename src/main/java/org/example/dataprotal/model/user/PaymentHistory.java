package org.example.dataprotal.model.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dataprotal.enums.PaymentStatus;
import org.example.dataprotal.enums.PaymentType;
import org.example.dataprotal.enums.Subscription;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal amount;
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @Enumerated(EnumType.STRING)
    private Subscription subscription;
    private Long userId;
}
