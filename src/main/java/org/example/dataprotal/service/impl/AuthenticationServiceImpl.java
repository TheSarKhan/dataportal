package org.example.dataprotal.service.impl;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.example.dataprotal.dto.request.LoginRequest;
import org.example.dataprotal.dto.request.RegisterRequest;
import org.example.dataprotal.dto.response.TokenResponse;
import org.example.dataprotal.email.service.EmailService;
import org.example.dataprotal.jwt.JwtService;
import org.example.dataprotal.model.enums.Roles;
import org.example.dataprotal.service.AuthenticationService;
import org.example.dataprotal.model.user.User;
import org.example.dataprotal.repository.user.UserRepository;

import org.example.dataprotal.redis.RedisService;
import org.example.dataprotal.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final CloudinaryService cloudinaryService;
    private final EmailService emailService;

    @Value("${spring.application.base-url}")
    private String baseUrl;
    @Override
    public String register(RegisterRequest request, MultipartFile profileImage) throws IOException, MessagingException {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();

        Set<Roles> roles = new HashSet<>();
        roles.add(Roles.USER);
        user.setRoles(roles);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setProfileImage(cloudinaryService.uploadFile(profileImage, "4sim-profileImage"));
        user.setAcceptTermsOfUse(request.getAcceptTerms());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setWorkplace(request.getWorkplace());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setVerified(false); // Başlangıçta aktif değil
        userRepository.save(user);

        String verificationToken = UUID.randomUUID().toString();
        System.out.println("TOKEN "+verificationToken);
        redisService.saveVerificationTokenToRedis(request.getEmail(), verificationToken, 10);
        String verificationUrl = baseUrl+"/api/v1/auth/verify?token=" + verificationToken;
        emailService.sendVerificationEmail(request.getEmail(), verificationUrl); // Mail gönderme
        return "Check your email to verify account";
    }


    @Override
    public TokenResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with email: " + request.getEmail());
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String accessToken = jwtService.generateAccessToken(user.getEmail(), null);
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
