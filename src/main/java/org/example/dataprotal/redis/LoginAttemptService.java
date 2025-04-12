package org.example.dataprotal.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final static int MAX_ATTEMPT = 3;
    private final static long EXPIRATION = 10 * 60; // 10 dakika

    public void loginFailed(String username) {
        String key = "login_attempt:" + username;
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        Integer attempts = (Integer) ops.get(key);
        System.out.println(attempts);
        if (attempts == null) attempts = 0;
        ops.set(key, attempts + 1, EXPIRATION, TimeUnit.SECONDS);
    }

    public void loginSucceeded(String username) {
        redisTemplate.delete("login_attempt:" + username);
    }

    public boolean isCaptchaRequired(String username) {
        Integer attempts = (Integer) redisTemplate.opsForValue().get("login_attempt:" + username);
        return attempts != null && attempts >= MAX_ATTEMPT;
    }
}

