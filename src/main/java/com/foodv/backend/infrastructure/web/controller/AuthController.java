package com.foodv.backend.infrastructure.web.controller;

import com.foodv.backend.domain.port.in.auth.LoginUseCase;
import com.foodv.backend.domain.port.in.auth.RefreshTokenUseCase;
import com.foodv.backend.domain.port.in.auth.RegisterUseCase;
import com.foodv.backend.infrastructure.config.JwtConfig;
import com.foodv.backend.infrastructure.persistence.repository.RefreshTokenRepository;
import com.foodv.backend.infrastructure.security.TokenBlacklistService;
import com.foodv.backend.infrastructure.web.dto.auth.AuthResponse;
import com.foodv.backend.infrastructure.web.dto.auth.LoginRequest;
import com.foodv.backend.infrastructure.web.dto.auth.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@Tag(name = "Autenticación")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtConfig jwtConfig;

    public AuthController(LoginUseCase loginUseCase,
                          RegisterUseCase registerUseCase,
                          RefreshTokenUseCase refreshTokenUseCase,
                          RefreshTokenRepository refreshTokenRepository,
                          TokenBlacklistService tokenBlacklistService,
                          JwtConfig jwtConfig) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenBlacklistService = tokenBlacklistService;
        this.jwtConfig = jwtConfig;
    }

    @Operation(summary = "Iniciar sesión")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "400", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "429", description = "Cuenta bloqueada por intentos fallidos")
    })
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

    @Operation(summary = "Registrar usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o email ya existe"),
            @ApiResponse(responseCode = "409", description = "Email ya registrado")
    })
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        registerUseCase.execute(new RegisterUseCase.RegisterCommand(
                request.nombres(),
                request.apellidos(),
                request.email(),
                request.password(),
                request.telefono(),
                request.role(),
                request.preferences(),
                request.restrictions(),
                request.budgetRange(),
                request.cuisineTypes()
        ));
        return ResponseEntity.status(201).body(Map.of("message", "Usuario registrado exitosamente"));
    }

    @Operation(summary = "Renovar access token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token renovado"),
            @ApiResponse(responseCode = "400", description = "Refresh token inválido o expirado")
    })
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

    @Operation(summary = "Cerrar sesión")
    @ApiResponse(responseCode = "200", description = "Sesión cerrada")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        // Revocar refresh token en BD
        String refreshToken = body.get("refreshToken");
        if (refreshToken != null) {
            refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            });
        }
        // Blacklistear access token en Redis
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            tokenBlacklistService.blacklist(accessToken, jwtConfig.getExpiration());
        }
        return ResponseEntity.ok(Map.of("message", "Sesión cerrada exitosamente"));
    }
}
