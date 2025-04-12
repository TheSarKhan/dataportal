package org.example.dataprotal.service;

import jakarta.mail.MessagingException;
import org.example.dataprotal.dto.request.LoginRequest;
import org.example.dataprotal.dto.request.RegisterRequest;
import org.example.dataprotal.dto.response.TokenResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AuthenticationService {
    String register(RegisterRequest request, MultipartFile profileImage) throws IOException, MessagingException;
    TokenResponse login(LoginRequest request);

}
