package org.example.dataprotal.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayriffInvoiceRequest {
    @NotNull
    private BigDecimal amount;
    @NotNull
    private String currency;
    @NotNull
    private String description;
    private String email;
    @NotNull
    private String phone;
    @NotNull
    private String orderId;
}
