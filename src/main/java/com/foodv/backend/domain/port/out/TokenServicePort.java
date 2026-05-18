package com.foodv.backend.domain.port.out;

import com.foodv.backend.domain.model.user.UserRole;

public interface TokenServicePort {

    default String generateAccessToken(Long userId, String email, UserRole role) {
        return generateAccessToken(userId, email, role, null);
    }

    String generateAccessToken(Long userId, String email, UserRole role, String nombres);

    String generateRefreshToken(String email);

    boolean isTokenValid(String token);

    String extractEmail(String token);

    String extractRole(String token);

    String extractNombres(String token);

    Long extractUserId(String token);

    long extractIssuedAtMillis(String token);

    long getAccessTokenExpirationMillis();

    long getRefreshTokenExpirationMillis();
}
