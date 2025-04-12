package org.example.dataprotal.redis;

import com.nimbusds.jose.util.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String ACCESS_TOKEN_PREFIX = "access-token:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh-token:";
    private static final String RESET_TOKEN_PREFIX = "reset-token:";
    private static final String VERIFY_TOKEN_PREFIX = "verify-token:";


    // 🔹 Kullanıcı için access token'ı Redis'e kaydetme
    public void saveAccessTokenToRedis(String username, String accessToken) {
        redisTemplate.opsForValue().set(ACCESS_TOKEN_PREFIX + username, accessToken);
    }

    // 🔹 Kullanıcının Redis'te saklanan access token'ını alma
    public String getAccessTokenFromRedis(String username) {
        return redisTemplate.opsForValue().get(ACCESS_TOKEN_PREFIX + username);
    }

    // 🔹 Kullanıcının access token'ını Redis'ten silme
    public void deleteAccessTokenFromRedis(String username) {
        redisTemplate.delete(ACCESS_TOKEN_PREFIX + username);
    }

    // 🔹 Refresh token'ı Redis'e kaydetme (örn: 7 gün geçerli)
    public void saveRefreshTokenToRedis(String username, String refreshToken, int timeout) {
        redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + username, refreshToken, timeout, TimeUnit.DAYS);
    }

    // 🔹 Refresh token'ı alma
    public String getRefreshTokenFromRedis(String username) {
        return redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + username);
    }

    // 🔹 Refresh token'ı silme
    public void deleteRefreshTokenFromRedis(String username) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + username);
    }

    // 🔹 Şifre sıfırlama için geçici token'ı kaydetme (örn: 10 dakika geçerli)
    public void saveResetTokenToRedis(String email, String token, int timeout) {
        redisTemplate.opsForValue().set(RESET_TOKEN_PREFIX + email, token, timeout, TimeUnit.MINUTES);
    }

    // 🔹 Şifre sıfırlama token'ını alma
    public String getResetTokenFromRedis(String email) {
        return redisTemplate.opsForValue().get(RESET_TOKEN_PREFIX + email);
    }

    // 🔹 Şifre sıfırlama token'ını silme
    public void deleteResetTokenFromRedis(String email) {
        redisTemplate.delete(RESET_TOKEN_PREFIX + email);
    }
    public void saveVerificationTokenToRedis(String email, String token, int minutes) {
        redisTemplate.opsForValue().set(VERIFY_TOKEN_PREFIX + token, email, Duration.ofMinutes(minutes));
    }

    public String getEmailByVerificationTokenFromRedis(String token) {
        return redisTemplate.opsForValue().get(VERIFY_TOKEN_PREFIX + token);
    }

    public void deleteVerificationTokenFromRedis(String token) {
        redisTemplate.delete(VERIFY_TOKEN_PREFIX + token);
    }

}
