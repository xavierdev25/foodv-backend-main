package com.foodv.backend.application.auth;

import com.foodv.backend.domain.port.in.auth.LoginUseCase;
import com.foodv.backend.domain.port.out.UserRepositoryPort;
import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.infrastructure.config.JwtConfig;
import com.foodv.backend.infrastructure.config.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginHandler implements LoginUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;

    public LoginHandler(UserRepositoryPort userRepositoryPort,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService,
                        JwtConfig jwtConfig) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtConfig = jwtConfig;
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

        return new LoginResult(accessToken, refreshToken, "Bearer", jwtConfig.getExpiration());
    }
}
