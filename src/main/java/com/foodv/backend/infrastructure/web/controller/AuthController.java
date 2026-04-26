package com.foodv.backend.infrastructure.web.controller;

import com.foodv.backend.domain.port.in.auth.LoginUseCase;
import com.foodv.backend.domain.port.in.auth.RefreshTokenUseCase;
import com.foodv.backend.domain.port.in.auth.RegisterUseCase;
import com.foodv.backend.infrastructure.persistence.repository.RefreshTokenRepository;
import com.foodv.backend.infrastructure.web.dto.auth.AuthResponse;
import com.foodv.backend.infrastructure.web.dto.auth.LoginRequest;
import com.foodv.backend.infrastructure.web.dto.auth.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthController(LoginUseCase loginUseCase,
                          RegisterUseCase registerUseCase,
                          RefreshTokenUseCase refreshTokenUseCase,
                          RefreshTokenRepository refreshTokenRepository) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginUseCase.LoginResult result = loginUseCase.execute(
                new LoginUseCase.LoginCommand(request.email(), request.password())
        );
        return ResponseEntity.ok(new AuthResponse(
                result.accessToken(),
                result.refreshToken(),
                result.tokenType(),
                result.expiresIn()
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        registerUseCase.execute(new RegisterUseCase.RegisterCommand(
                request.nombres(),
                request.apellidos(),
                request.email(),
                request.password(),
                request.telefono(),
                request.role()
        ));
        return ResponseEntity.status(201).body(Map.of("message", "Usuario registrado exitosamente"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> body) {
        LoginUseCase.LoginResult result = refreshTokenUseCase.execute(body.get("refreshToken"));
        return ResponseEntity.ok(new AuthResponse(
                result.accessToken(),
                result.refreshToken(),
                result.tokenType(),
                result.expiresIn()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken != null) {
            refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            });
        }
        return ResponseEntity.ok(Map.of("message", "Sesión cerrada exitosamente"));
    }
}
