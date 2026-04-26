package com.foodv.backend.application.auth;

import com.foodv.backend.domain.port.in.auth.LoginUseCase;
import com.foodv.backend.domain.port.out.UserRepositoryPort;
import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.infrastructure.config.JwtConfig;
import com.foodv.backend.infrastructure.config.JwtService;
import com.foodv.backend.infrastructure.persistence.entity.RefreshTokenEntity;
import com.foodv.backend.infrastructure.persistence.repository.RefreshTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginHandler implements LoginUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginHandler(UserRepositoryPort userRepositoryPort,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService,
                        JwtConfig jwtConfig,
                        RefreshTokenRepository refreshTokenRepository) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtConfig = jwtConfig;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public LoginResult execute(LoginCommand command) {
        User user = userRepositoryPort.findByEmail(command.email())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        if (!user.isActivo()) {
            throw new IllegalArgumentException("Usuario inactivo");
        }

        String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .token(refreshToken)
                .userId(user.getId())
                .expiresAt(LocalDateTime.now().plusSeconds(jwtConfig.getRefreshExpiration() / 1000))
                .revoked(false)
                .creadoEn(LocalDateTime.now())
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        return new LoginResult(accessToken, refreshToken, "Bearer", jwtConfig.getExpiration());
    }
}
