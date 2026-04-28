package com.foodv.backend.infrastructure.security;

import com.foodv.backend.domain.port.out.LoginAttemptPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Implementación distribuida del rate limit por intentos de login usando Redis.
 * Reemplaza al ConcurrentHashMap en memoria que no funcionaba con múltiples réplicas.
 */
@Component
public class RedisLoginAttemptAdapter implements LoginAttemptPort {

    private static final String PREFIX = "login:fail:";
    private static final int MAX_ATTEMPTS = 5;
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(15);

    private final StringRedisTemplate redisTemplate;

    public RedisLoginAttemptAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean isBlocked(String email) {
        String key = key(email);
        String count = redisTemplate.opsForValue().get(key);
        if (count == null) return false;
        try {
            return Integer.parseInt(count) >= MAX_ATTEMPTS;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void recordFailedAttempt(String email) {
        String key = key(email);
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redisTemplate.expire(key, BLOCK_DURATION);
        }
    }

    @Override
    public void resetAttempts(String email) {
        redisTemplate.delete(key(email));
    }

    private String key(String email) {
        return PREFIX + email.toLowerCase();
    }
}
