package org.example.dataprotal.dto;

import java.math.BigDecimal;
import java.util.List;

public record SubscriptionDataDto(BigDecimal priceForOneMonth,
                                  BigDecimal priceForOneYear,
                                  List<String> advantages,
                                  boolean isActive) {
}
