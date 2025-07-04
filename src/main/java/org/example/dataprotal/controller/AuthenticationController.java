package org.example.dataprotal.controller;

import com.cloudinary.utils.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.example.dataprotal.config.RecaptchaConfig;
import org.example.dataprotal.dto.request.LoginRequest;

import org.example.dataprotal.dto.request.RegisterRequest;
import org.example.dataprotal.dto.response.TokenResponse;
import org.example.dataprotal.jwt.JwtService;
import org.example.dataprotal.redis.LoginAttemptService;
import org.example.dataprotal.service.AuthenticationService;
import org.example.dataprotal.model.user.User;
import org.example.dataprotal.repository.user.UserRepository;
import org.example.dataprotal.redis.RedisService;
import org.example.dataprotal.service.impl.CustomOAuth2UserServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final RecaptchaConfig recaptchaConfig;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RedisService redisService;
    private final CustomOAuth2UserServiceImpl customOAuth2UserServiceImpl;

    @PostMapping("/register")
    @Operation(summary = "Qeydiyyat üçün endpoint")

    public ResponseEntity<?> register(@RequestPart RegisterRequest registerRequest, @RequestPart MultipartFile profileImage) throws IOException, MessagingException {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already in use");
        }
        String response = authenticationService.register(registerRequest,profileImage);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/login")
    @Operation(summary = "Giriş üçün endpoint")

    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        String email = loginRequest.getEmail();

        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(loginRequest.getPassword())) {
            return ResponseEntity.badRequest().body("Email and password are required");
        }

        Optional<User> userOpt = userRepository.findByEmail(email);

        // CAPTCHA gerekli mi kontrol et
        if (loginAttemptService.isCaptchaRequired(email)) {
            if (StringUtils.isEmpty(loginRequest.getRecaptchaToken()) ||
                    !recaptchaConfig.verifyCaptcha(loginRequest.getRecaptchaToken())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("ReCaptcha validation failed");
            }
        }

        // Kullanıcı yoksa bile deneme sayısı artmalı
        if (userOpt.isEmpty()) {
            loginAttemptService.loginFailed(email);
            return ResponseEntity.badRequest().body("Invalid email or password");
        }

        User user = userOpt.get();

        if (!user.isVerified()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please verify your email before logging in.");
        }

        if (!user.isActive()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please activate your account before logging in.");
        }

        try {
            TokenResponse tokenResponse = authenticationService.login(loginRequest);
            loginAttemptService.loginSucceeded(email); // Başarılı girişte resetle
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            System.out.println("Salamlar");
            loginAttemptService.loginFailed(email);
            return ResponseEntity.badRequest().body("Invalid email or password");
        }
    }



    @PostMapping("/google-login")
    @Operation(summary = "Google linki istifadə edərək asan login üçün endpoint")
    public ResponseEntity<TokenResponse> googleLogin(@RequestBody Map<String, String> body) throws Exception {
        System.out.println(googleClientId);
        String idToken = body.get("id_token");
        try {
            TokenResponse response = customOAuth2UserServiceImpl.processGoogleLogin(idToken);
            return ResponseEntity.ok(response);
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/oauth2-failure")
    @Operation(summary = "Giriş xətası göstərmək üçün endpoint")

    public ResponseEntity<String> oauth2Failure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OAuth2 login failed.");
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token göndərərək yeni Access və Refresh token almaq üçün endpoint")

    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        String email = jwtService.extractEmail(refreshToken);
        System.out.println("REFRESH TOKEN: " + refreshToken);

        if (email.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }
        String storedRefreshToken = redisService.getRefreshTokenFromRedis(email);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            return ResponseEntity.status(401).body("Refresh token mismatch or expired");
        }
        String newAccessToken = jwtService.generateAccessToken(email, null);
        String newRefreshToken = jwtService.generateRefreshToken(email);
        System.out.println("REFRESH TOKEN: " + newRefreshToken);
        redisService.deleteRefreshTokenFromRedis(email);
        redisService.saveRefreshTokenToRedis(email, newRefreshToken, 7); // 7 gün geçerli
         return ResponseEntity.ok(TokenResponse.builder().accessToken(newAccessToken).refreshToken(newRefreshToken).build());
    }
    @GetMapping("/verify")
    @Operation(summary = "Email doğrulaması üçün endpoint")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        String email = redisService.getEmailByVerificationTokenFromRedis(token);
        if (email == null) {
            return ResponseEntity.status(400).body("Invalid or expired token");
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOptional.get();
        user.setVerified(true);
        userRepository.save(user);
        redisService.deleteVerificationTokenFromRedis(token);

        return ResponseEntity.ok("Email verified successfully. You can now log in.");
    }


}
