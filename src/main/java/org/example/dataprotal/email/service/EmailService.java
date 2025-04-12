package org.example.dataprotal.email.service;

import jakarta.mail.MessagingException;

import java.io.IOException;

public interface EmailService {
     void sendVerificationEmail(String to, String link) throws MessagingException, IOException;
}
