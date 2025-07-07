package org.example.dataprotal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public enum Subscription {
    FREE(BigDecimal.valueOf(0), BigDecimal.valueOf(0)),
    STANDARD(BigDecimal.valueOf(10), BigDecimal.valueOf(96)),
    PREMIUM(BigDecimal.valueOf(0), BigDecimal.valueOf(0));

    BigDecimal priceForOneMonth;

    BigDecimal priceForOneYear;

}