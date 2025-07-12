package org.example.dataprotal.payment.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class PayriffConfig {

    @Value("${payriff.api.key}")
    private String apiKey;

    @Value("${payriff.api.url}")
    private String apiUrl;

    @Value("${payriff.merchant.id}")
    private String merchantId;

    @Value("${payriff.approve.url}")
    private String successUrl;

    @Value("${payriff.cancel.url}")
    private String cancelUrl;

    @Value("${payriff.decline.url}")
    private String declineUrl;
}
