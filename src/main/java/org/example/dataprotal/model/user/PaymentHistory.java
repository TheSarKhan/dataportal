package org.example.dataprotal.model.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.dataprotal.enums.PaymentStatus;
import org.example.dataprotal.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_history")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    BigDecimal amount;

    LocalDateTime date;

    @Enumerated(EnumType.STRING)
    PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    PaymentStatus paymentStatus;

    Long subscriptionId;

    Long userId;

    String billUrl;
}
