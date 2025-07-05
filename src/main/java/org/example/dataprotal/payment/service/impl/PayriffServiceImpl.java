package org.example.dataprotal.payment.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dataprotal.payment.config.PayriffConfig;
import org.example.dataprotal.payment.dto.PayriffInvoiceRequest;
import org.example.dataprotal.payment.service.PayriffService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayriffServiceImpl implements PayriffService {
    private final PayriffConfig payriffConfig;
    private final RestTemplate restTemplate=new RestTemplate();
    @Override
    public String createInvoice(PayriffInvoiceRequest payriffInvoiceRequest, String token) {
        return "";
    }

}
