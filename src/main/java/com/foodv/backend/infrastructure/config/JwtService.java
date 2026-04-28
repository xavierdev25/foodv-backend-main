package com.foodv.backend.infrastructure.config;

import com.foodv.backend.domain.model.user.UserRole;
import com.foodv.backend.domain.port.out.TokenServicePort;
import org.springframework.stereotype.Service;

/**
 * Wrapper legacy alrededor del {@link TokenServicePort} para mantener compatibilidad.
 * Nuevos clientes deben inyectar {@link TokenServicePort} directamente.
 */
@Service
public class JwtService {

    private final TokenServicePort tokenServicePort;

    public JwtService(TokenServicePort tokenServicePort) {
        this.tokenServicePort = tokenServicePort;
    }

    public String generateAccessToken(String email, UserRole role) {
        return tokenServicePort.generateAccessToken(email, role);
    }

    public String generateRefreshToken(String email) {
        return tokenServicePort.generateRefreshToken(email);
    }

    public boolean isTokenValid(String token) {
        return tokenServicePort.isTokenValid(token);
    }

    public String extractEmail(String token) {
        return tokenServicePort.extractEmail(token);
    }

    public String extractRole(String token) {
        return tokenServicePort.extractRole(token);
    }
}
