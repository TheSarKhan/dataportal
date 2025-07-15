package org.example.dataprotal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.example.dataprotal.dto.response.PaymentHistoryResponse;
import org.example.dataprotal.enums.PaymentStatus;
import org.example.dataprotal.enums.PaymentType;
import org.example.dataprotal.model.user.PaymentHistory;
import org.example.dataprotal.service.PaymentHistoryService;
import org.example.dataprotal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/payment-history")
@Tag(name = "Payment History",
        description = "Operations related to viewing and filtering user payment history")
public class PaymentHistoryController {
    private final PaymentHistoryService paymentHistoryService;

    private final UserService userService;

    @GetMapping("/getAll")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all payment histories",
            description = "Returns all payment history records. Accessible only by ADMIN.")
    public List<PaymentHistory> getAll() {
        return paymentHistoryService.getAll();
    }


    @GetMapping
    @Operation(summary = "Get current user's payment history",
            description = "Returns the payment history of the currently authenticated user.")
    public ResponseEntity<PaymentHistoryResponse> getPaymentHistory() throws AuthException {
        return ResponseEntity.ok(paymentHistoryService.getPaymentHistoryByUser(userService.getCurrentUser()));
    }

    @GetMapping("/filter")
    @Operation(
            summary = "Filter payment history",
            description = "Filters payment history of the current user by multiple optional parameters including status, type, subscription, date range, and amount range."
    )
    public List<PaymentHistory> filterPayments(@RequestParam(required = false) PaymentStatus status,
                                               @RequestParam(required = false) PaymentType paymentType,
                                               @RequestParam(required = false) Long subscriptionId,
                                               @RequestParam(required = false) LocalDate fromDate,
                                               @RequestParam(required = false) LocalDate toDate,
                                               @RequestParam(required = false) BigDecimal minAmount,
                                               @RequestParam(required = false) BigDecimal maxAmount
    ) throws AuthException {
        return paymentHistoryService.filterPayments(userService.getCurrentUser(),
                status, paymentType, subscriptionId, fromDate, toDate, minAmount, maxAmount);
    }
}
