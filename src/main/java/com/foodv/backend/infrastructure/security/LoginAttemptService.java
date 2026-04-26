package com.foodv.backend.infrastructure.security;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int BLOCK_DURATION_MINUTES = 15;

    private record AttemptInfo(int count, LocalDateTime lastAttempt) {}

    private final Map<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

    public void recordFailedAttempt(String email) {
        attempts.compute(email, (key, existing) -> {
            if (existing == null) {
                return new AttemptInfo(1, LocalDateTime.now());
            }
            return new AttemptInfo(existing.count() + 1, LocalDateTime.now());
        });
    }

    public void resetAttempts(String email) {
        attempts.remove(email);
    }

    public boolean isBlocked(String email) {
        AttemptInfo info = attempts.get(email);
        if (info == null) return false;
        if (info.count() < MAX_ATTEMPTS) return false;
        if (info.lastAttempt().isBefore(LocalDateTime.now().minusMinutes(BLOCK_DURATION_MINUTES))) {
            attempts.remove(email);
            return false;
        }
        return true;
    }

    public int getRemainingAttempts(String email) {
        AttemptInfo info = attempts.get(email);
        if (info == null) return MAX_ATTEMPTS;
        return Math.max(0, MAX_ATTEMPTS - info.count());
    }
}