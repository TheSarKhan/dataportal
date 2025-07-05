package org.example.dataprotal.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayriffInvoiceRequest {
    @NotBlank
    private String amount;
    @NotBlank
    private String currency;
    @NotBlank
    private String description;
    @NotBlank
    private String orderId;
}
