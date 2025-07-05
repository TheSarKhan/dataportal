package org.example.dataprotal.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayriffInvoiceResponse {

    private String responseCode;
    private String message;
    private PayriffData payriffData;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayriffData {
        private String invoiceId;
        private String invoiceUrl;
        private String status;
    }
}
