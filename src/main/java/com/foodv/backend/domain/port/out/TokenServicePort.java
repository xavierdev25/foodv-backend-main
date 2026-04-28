package com.foodv.backend.domain.port.out;

import com.foodv.backend.domain.model.user.UserRole;

public interface TokenServicePort {

    String generateAccessToken(String email, UserRole role);

    String generateRefreshToken(String email);

    boolean isTokenValid(String token);

    String extractEmail(String token);

    String extractRole(String token);

    long getAccessTokenExpirationMillis();

    long getRefreshTokenExpirationMillis();
}
