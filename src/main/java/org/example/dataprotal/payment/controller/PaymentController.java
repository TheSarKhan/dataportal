package org.example.dataprotal.payment.controller;

import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dataprotal.exception.InvoiceCanNotBeCreatedException;
import org.example.dataprotal.model.user.User;
import org.example.dataprotal.payment.dto.PayriffInvoiceRequest;
import org.example.dataprotal.payment.service.PayriffService;
import org.example.dataprotal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {
    private final PayriffService payriffService;
    private final UserService userService;

    @PostMapping("/create-invoice")
    public ResponseEntity<String> createInvoice(@RequestBody @Valid PayriffInvoiceRequest payriffInvoiceRequest) throws AuthException, InvoiceCanNotBeCreatedException {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(payriffService.createInvoiceWithUser(payriffInvoiceRequest, currentUser));
    }
}
