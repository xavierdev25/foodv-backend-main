package com.foodv.backend.application.auth;

import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.domain.port.in.auth.LoginUseCase;
import com.foodv.backend.domain.port.in.auth.RefreshTokenUseCase;
import com.foodv.backend.domain.port.out.UserRepositoryPort;
import com.foodv.backend.infrastructure.config.JwtConfig;
import com.foodv.backend.infrastructure.config.JwtService;
import com.foodv.backend.infrastructure.persistence.entity.RefreshTokenEntity;
import com.foodv.backend.infrastructure.persistence.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RefreshTokenHandler implements RefreshTokenUseCase {

    private final JwtService jwtService;
    private final UserRepositoryPort userRepositoryPort;
    private final JwtConfig jwtConfig;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenHandler(JwtService jwtService,
                               UserRepositoryPort userRepositoryPort,
                               JwtConfig jwtConfig,
                               RefreshTokenRepository refreshTokenRepository) {
        this.jwtService = jwtService;
        this.userRepositoryPort = userRepositoryPort;
        this.jwtConfig = jwtConfig;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public LoginUseCase.LoginResult execute(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Refresh token inválido");
        }

        RefreshTokenEntity entity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token inválido"));

        if (entity.isRevoked()) {
            throw new IllegalArgumentException("Refresh token revocado");
        }

        if (entity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh token expirado");
        }

        refreshTokenRepository.save(entity.toBuilder().revoked(true).build());

        String email = jwtService.extractEmail(refreshToken);

        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String newAccessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole());
        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());

        RefreshTokenEntity newRefreshTokenEntity = RefreshTokenEntity.builder()
                .token(newRefreshToken)
                .userId(user.getId())
                .expiresAt(LocalDateTime.now().plusSeconds(jwtConfig.getRefreshExpiration() / 1000))
                .revoked(false)
                .creadoEn(LocalDateTime.now())
                .build();
        refreshTokenRepository.save(newRefreshTokenEntity);

        return new LoginUseCase.LoginResult(newAccessToken, newRefreshToken, "Bearer", jwtConfig.getExpiration());
    }
}
