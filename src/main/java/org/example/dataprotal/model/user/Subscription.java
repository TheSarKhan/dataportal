package org.example.dataprotal.model.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subscriptions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @JdbcTypeCode(SqlTypes.JSON)
    Map<String, List<BigDecimal>> cureencyMonthlyAndYearlyPriceMap;

    @JdbcTypeCode(SqlTypes.JSON)
    List<String> advantages;
}
