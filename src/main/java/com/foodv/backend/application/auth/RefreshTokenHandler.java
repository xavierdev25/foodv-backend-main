package com.foodv.backend.application.auth;

import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.domain.port.in.auth.LoginUseCase;
import com.foodv.backend.domain.port.in.auth.RefreshTokenUseCase;
import com.foodv.backend.domain.port.out.UserRepositoryPort;
import com.foodv.backend.infrastructure.config.JwtConfig;
import com.foodv.backend.infrastructure.config.JwtService;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenHandler implements RefreshTokenUseCase {

    private final JwtService jwtService;
    private final UserRepositoryPort userRepositoryPort;
    private final JwtConfig jwtConfig;

    public RefreshTokenHandler(JwtService jwtService,
                               UserRepositoryPort userRepositoryPort,
                               JwtConfig jwtConfig) {
        this.jwtService = jwtService;
        this.userRepositoryPort = userRepositoryPort;
        this.jwtConfig = jwtConfig;
    }

    @Override
    public LoginUseCase.LoginResult execute(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Refresh token inválido");
        }

        String email = jwtService.extractEmail(refreshToken);

        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String newAccessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole());
        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new LoginUseCase.LoginResult(newAccessToken, newRefreshToken, "Bearer", jwtConfig.getExpiration());
    }
}
