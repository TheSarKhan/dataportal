package org.example.dataprotal.jwt.controller;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import org.example.dataprotal.jwt.dto.request.LoginDTO;

import org.example.dataprotal.jwt.dto.request.RegisterDTO;
import org.example.dataprotal.jwt.dto.response.TokenResponse;
import org.example.dataprotal.jwt.JwtUtil;
import org.example.dataprotal.jwt.service.service.AuthenticationService;
import org.example.dataprotal.model.user.User;
import org.example.dataprotal.repository.user.UserRepository;
import org.example.dataprotal.redis.RedisService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthorizationController {

    // private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authService;
    private final RedisService redisService;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        TokenResponse tokenResponse = authenticationService.login(request);
        if (request.getEmail() == null || request.getPassword() == null) {
            ResponseEntity.status(400).body("Email or Password is null");
        }
        if (user.isEmpty()) {
            ResponseEntity.status(400).body("Email does not exist");
        }
        if (!Objects.equals(request.getPassword(), user.get().getPassword())) {
            ResponseEntity.status(400).body("Passwords do not match");
        }
        return ResponseEntity.status(200).body(tokenResponse);
    }


    @PostMapping("/register")
    public TokenResponse register(@Valid @RequestBody RegisterDTO request) {
        return authService.register(request);


//    @PostMapping("/refresh-token")
//    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
//        String refreshToken = request.get("refreshToken");
//
//        if (refreshToken == null || !jwtUtil.isTokenValid(refreshToken, jwtUtil.extractUsername(refreshToken))) {
//            return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired refresh token"));
//        }
//
//        String email = jwtUtil.extractUsername(refreshToken);
//        String newAccessToken = jwtUtil.generateAccessToken(email, null);
//
//        Map<String, String> tokens = new HashMap<>();
//        tokens.put("accessToken", newAccessToken);
//        tokens.put("refreshToken", refreshToken);
//
//        return ResponseEntity.ok(tokens);
//    }

//    @PostMapping("/refresh-token")
//    public ResponseEntity<Map<String, String>> refreshToken(
//            @RequestBody RefreshTokenRequest request) {
//        System.out.println("Received request: " + request);
//
//        String refreshToken = request.getRefreshToken();
//        System.out.println("Refresh token: " + refreshToken);
//
//        if (refreshToken == null || !jwtUtil.isTokenValid(refreshToken, jwtUtil.extractUsername(refreshToken))) {
//            System.out.println("Token is invalid or null");
//            return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired refresh token"));
//        }
//
//        try {
//            String email = jwtUtil.extractUsername(refreshToken);
//            System.out.println("Extracted email: " + email);
//
//            String newAccessToken = jwtUtil.generateAccessToken(email, null);
//            System.out.println("Generated new access token: " + newAccessToken);
//
//            Map<String, String> tokens = new HashMap<>();
//            tokens.put("accessToken", newAccessToken);
//            tokens.put("refreshToken", refreshToken);
//
//            return ResponseEntity.ok(tokens);
//        } catch (Exception e) {
//            System.out.println("Error in refreshToken: " + e.getMessage());
//            return ResponseEntity.status(500).body(Map.of("error", "Internal server error: " + e.getMessage()));
//        }
    }


    @PostMapping("/refresh-token")
    public TokenResponse refresh(@RequestHeader("Authorization") String token) {
        token = token.substring(7);
        String email = jwtUtil.extractEmail(token);
        String accessToken = jwtUtil.generateAccessToken(token, null);
        String refreshToken = jwtUtil.generateRefreshToken(token);
        Optional<User> userOptional = userRepository.findByEmail(email);
         redisService.saveTokenToRedis(accessToken, email);
        return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }


    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth endpoint is working!");
    }
}