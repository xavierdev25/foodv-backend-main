package com.foodv.backend.domain.port.out;

public interface LoginAttemptPort {

    boolean isBlocked(String email);

    void recordFailedAttempt(String email);

    void resetAttempts(String email);
}
