package org.example.dataprotal.dto.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record SubscriptionRequest(String name,
                                  Map<String, List<BigDecimal>> cureencyMonthlyAndYearlyPriceMap,
                                  List<String> advantages) {
}
