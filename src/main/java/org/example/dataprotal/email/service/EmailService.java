package org.example.dataprotal.email.service;

import jakarta.mail.MessagingException;
import org.example.dataprotal.email.dto.AppealEmail;
import org.example.dataprotal.email.dto.ConsultationEmail;

public interface EmailService {
    void sendConsultation(ConsultationEmail consultationEmail) throws MessagingException;
    void sendAppeal(AppealEmail appealEmail) throws MessagingException;
}