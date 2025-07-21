package org.example.dataprotal.payment.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dataprotal.exception.InvoiceCanNotBeCreatedException;
import org.example.dataprotal.exception.ResourceCanNotFoundException;
import org.example.dataprotal.jwt.JwtService;
import org.example.dataprotal.model.user.User;
import org.example.dataprotal.payment.config.PayriffConfig;
import org.example.dataprotal.payment.dto.PayriffInvoiceRequest;
import org.example.dataprotal.payment.dto.PayriffInvoiceResponse;
import org.example.dataprotal.payment.service.PayriffService;
import org.example.dataprotal.repository.user.UserRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayriffServiceImpl implements PayriffService {
    private final PayriffConfig payriffConfig;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RestTemplate restTemplate;

    @Override
    public String createInvoiceWithUser(PayriffInvoiceRequest payriffInvoiceRequest, User user) throws InvoiceCanNotBeCreatedException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtService.generateAccessToken(user.getEmail(), null));

        return makePayment(payriffInvoiceRequest, user, headers);
    }

    private String makePayment(PayriffInvoiceRequest payriffInvoiceRequest, User user, HttpHeaders headers) throws InvoiceCanNotBeCreatedException {
        HttpEntity<Map<String, Object>> httpEntity = getMapHttpEntity(payriffInvoiceRequest, user, headers);

        ResponseEntity<PayriffInvoiceResponse> responseResponseEntity = restTemplate.postForEntity(
                payriffConfig.getApiUrl(), httpEntity, PayriffInvoiceResponse.class);
        if (responseResponseEntity.getStatusCode() == HttpStatus.OK && responseResponseEntity.getBody() != null) {
            PayriffInvoiceResponse body = responseResponseEntity.getBody();
            if (body.getPayriffData() != null) {
                return body.getPayriffData().getInvoiceUrl();
            }
        }
        throw new InvoiceCanNotBeCreatedException("Invoice can not be created");
    }

    private HttpEntity<Map<String, Object>> getMapHttpEntity(PayriffInvoiceRequest payriffInvoiceRequest, User user, HttpHeaders headers) {
        Map<String, Object> payloads = new HashMap<>();
        payloads.put("merchant", payriffConfig.getMerchantId());
        payloads.put("signature", generateSignature(payriffInvoiceRequest.getOrderId(), payriffInvoiceRequest.getAmount()));
        payloads.put("firstName", user.getFirstName());
        payloads.put("lastName", user.getLastName());
        payloads.put("amount", payriffInvoiceRequest.getAmount());
        payloads.put("currency", payriffInvoiceRequest.getCurrency());
        payloads.put("description", payriffInvoiceRequest.getDescription());
        payloads.put("email", user.getEmail());
        payloads.put("phone", user.getPhoneNumber());
        payloads.put("orderId", payriffInvoiceRequest.getOrderId());
        payloads.put("approveUrl", payriffConfig.getSuccessUrl());
        payloads.put("cancelUrl", payriffConfig.getCancelUrl());
        payloads.put("declineUrl", payriffConfig.getDeclineUrl());

        return new HttpEntity<>(payloads, headers);
    }

    private String generateSignature(String orderId, String amount) {
        if (orderId == null || orderId.isBlank() || amount == null || amount.isBlank()) {
            throw new IllegalArgumentException("Order Id and amount must not be blank");
        }
        String data = payriffConfig.getMerchantId() + orderId + amount + payriffConfig.getApiKey();
        return DigestUtils.md5DigestAsHex(data.getBytes(StandardCharsets.UTF_8));
    }

}

