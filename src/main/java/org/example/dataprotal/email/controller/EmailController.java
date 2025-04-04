package org.example.dataprotal.email.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.example.dataprotal.email.dto.AppealEmail;
import org.example.dataprotal.email.dto.ConsultationEmail;
import org.example.dataprotal.email.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/email/consultation")
    @Operation(description = "Konsultasiya maili göndərmək üçün (Həm user həm admin)")
    ResponseEntity<?> sendConsultationMail(@RequestBody ConsultationEmail consultationEmail) throws MessagingException {
        emailService.sendConsultation(consultationEmail);
        return ResponseEntity.status(201).body("Consultation mail sent");
    }

    @PostMapping("/email/appeal")
    @Operation(description = "Müraciət maili göndərmək üçün (Həm user həm admin)")
    ResponseEntity<?> sendAppealMail(@RequestBody AppealEmail appealEmail) throws MessagingException {
        emailService.sendAppeal(appealEmail);
        return ResponseEntity.status(201).body("Appeal mail sent");
    }
}
