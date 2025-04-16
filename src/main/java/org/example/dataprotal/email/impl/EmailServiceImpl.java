package org.example.dataprotal.email.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.example.dataprotal.email.service.EmailService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;
    @Value("${spring.application.base-url}")
    private String baseUrl;

    @Override
    public void sendVerificationEmail(String to, String link) throws MessagingException, IOException {
        // HTML şablon dosyasını oku
        ClassPathResource resource = new ClassPathResource("templates/email/verification_email.html");
        InputStream inputStream = resource.getInputStream();

        // HTML içeriğini al
        String htmlContent = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        String supportLink = baseUrl + "/support";
        // Dinamik olarak doğrulama linkini ekle
        htmlContent = htmlContent.replace("{verificationLink}", link);
        htmlContent = htmlContent.replace("{supportLink}", supportLink);

        // MimeMessage ile HTML içerik gönder
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("Verify your email");
        helper.setFrom(from);
        helper.setText(htmlContent, true); // HTML formatında gönderme

        mailSender.send(message);
    }
}
