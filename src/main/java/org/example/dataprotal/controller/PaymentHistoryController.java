package org.example.dataprotal.controller;

import lombok.RequiredArgsConstructor;
import org.example.dataprotal.enums.PaymentStatus;
import org.example.dataprotal.enums.PaymentType;
import org.example.dataprotal.enums.Subscription;
import org.example.dataprotal.exception.ResourceCanNotFoundException;
import org.example.dataprotal.model.paymenthistory.PaymentHistory;
import org.example.dataprotal.service.PaymentHistoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("payment_history")
@RequiredArgsConstructor
public class PaymentHistoryController {
    private final PaymentHistoryService paymentHistoryService;

    @GetMapping("/getAll")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PaymentHistory> getAll() {
        return paymentHistoryService.getAll();
    }

    @GetMapping
    public List<PaymentHistory> filterPayments(@RequestParam(required = false) PaymentStatus status,
                                               @RequestParam(required = false) PaymentType paymentType,
                                               @RequestParam(required = false) Subscription subscription,
                                               @RequestParam(required = false) LocalDate fromDate,
                                               @RequestParam(required = false) LocalDate toDate,
                                               @RequestParam(required = false) BigDecimal minAmount,
                                               @RequestParam(required = false) BigDecimal maxAmount
    ) throws ResourceCanNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return paymentHistoryService.filterPayments(email,status,paymentType,subscription,fromDate,toDate,minAmount,maxAmount);
    }
}
