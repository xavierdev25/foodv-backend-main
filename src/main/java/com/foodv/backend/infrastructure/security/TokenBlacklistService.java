package com.foodv.backend.infrastructure.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TokenBlacklistService {

    private static final String PREFIX = "blacklist:";
    private final StringRedisTemplate redisTemplate;

    public TokenBlacklistService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklist(String token, long expirationMillis) {
        redisTemplate.opsForValue().set(
                PREFIX + token,
                "revoked",
                Duration.ofMillis(expirationMillis)
        );
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + token));
    }
}