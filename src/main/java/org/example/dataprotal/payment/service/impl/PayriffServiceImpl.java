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
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayriffServiceImpl implements PayriffService {
    private final PayriffConfig payriffConfig;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String createInvoice(PayriffInvoiceRequest payriffInvoiceRequest, String token) throws ResourceCanNotFoundException, InvoiceCanNotBeCreatedException {
        String email = jwtService.extractEmail(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            log.error("User can not found with email {}", email);
            return new ResourceCanNotFoundException("User can not found with email {}" + email);
        });
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer" + token);

        return makePayment(payriffInvoiceRequest, user, headers);
    }

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
}

