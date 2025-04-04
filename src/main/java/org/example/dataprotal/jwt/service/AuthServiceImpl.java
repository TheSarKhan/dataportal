package org.example.dataprotal.jwt.service;

import lombok.RequiredArgsConstructor;
import org.example.dataprotal.jwt.dto.request.LoginDTO;
import org.example.dataprotal.jwt.dto.request.RegisterDTO;
import org.example.dataprotal.jwt.dto.response.TokenResponse;
import org.example.dataprotal.jwt.JwtUtil;
import org.example.dataprotal.jwt.service.service.AuthenticationService;
import org.example.dataprotal.model.user.User;
import org.example.dataprotal.model.user.repository.UserRepository;

import org.example.dataprotal.redis.RedisService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;


    public TokenResponse register(RegisterDTO request) {
        // E-posta kontrolü
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("E-posta zaten kayıtlı!");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Şifrə boş ola bilməz!");
        }
        // Şifrə və təsdiqləmə şifrəsi eyni olmalıdır
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Şifrə və təsdiqləmə şifrəsi eyni olmalıdır!");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email zaten kayıtlı!");
        }
        LocalDateTime now = LocalDateTime.now();
        String refreshToken = jwtUtil.generateRefreshToken(request.getEmail());
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setWorkplace(request.getWorkplace());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);


        String accessToken = jwtUtil.generateAccessToken(request.getEmail(), null); // Claims kısmı null olabilir

        redisService.saveTokenToRedis(accessToken, request.getEmail());
        String savedToken = redisService.getTokenFromRedis(request.getEmail());
        if (savedToken != null) {
            System.out.println("Redis'ten alınan token: " + savedToken);
        } else {
            System.out.println("Token Redis'ten alınamadı");
        }
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

//        LocalDateTime now = LocalDateTime.now();
//        String refreshToken = jwtUtil.generateRefreshToken(request.getEmail());
//
//        // Kullanıcı oluşturma ve veritabanına kaydetme
//        User user = new User();
//        user.setFirstName(request.getFirstName());
//        user.setLastName(request.getLastName());
//        user.setEmail(request.getEmail());
//        user.setWorkplace(request.getWorkplace());
//        user.setPhoneNumber(request.getPhoneNumber());
//        user.setCreatedAt(now);
//        user.setUpdatedAt(now);
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setRefreshToken(refreshToken);
//        userRepository.save(user);
//
//        String accessToken = jwtUtil.generateAccessToken(request.getEmail(), null); // Claims kısmı null olabilir
//
//        return TokenResponse.builder()
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .build();
//    }

    @Override
    public TokenResponse login(LoginDTO request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with username: " + request.getEmail());
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), null);
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}