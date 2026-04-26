package com.foodv.backend.application.auth;

import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.domain.model.user.UserRole;
import com.foodv.backend.domain.port.in.auth.LoginUseCase;
import com.foodv.backend.domain.port.out.UserRepositoryPort;
import com.foodv.backend.infrastructure.config.JwtConfig;
import com.foodv.backend.infrastructure.config.JwtService;
import com.foodv.backend.infrastructure.persistence.repository.RefreshTokenRepository;
import com.foodv.backend.infrastructure.security.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginHandler - Autenticación")
class LoginHandlerTest {

    @Mock private UserRepositoryPort userRepositoryPort;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private JwtConfig jwtConfig;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private LoginAttemptService loginAttemptService;

    @InjectMocks private LoginHandler loginHandler;

    private User activeUser;

    @BeforeEach
    void setUp() {
        activeUser = User.builder()
                .id(1L)
                .nombres("Xavier")
                .apellidos("David")
                .email("xavier@foodv.com")
                .password("hashedPassword")
                .role(UserRole.ADMIN)
                .activo(true)
                .creadoEn(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Login exitoso retorna tokens")
    void login_exitoso_retorna_tokens() {
        when(loginAttemptService.isBlocked(anyString())).thenReturn(false);
        when(userRepositoryPort.findByEmail("xavier@foodv.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtService.generateAccessToken(anyString(), any())).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(anyString())).thenReturn("refreshToken");
        when(jwtConfig.getExpiration()).thenReturn(86400000L);
        when(jwtConfig.getRefreshExpiration()).thenReturn(604800000L);
        when(refreshTokenRepository.save(any())).thenReturn(null);

        LoginUseCase.LoginResult result = loginHandler.execute(
                new LoginUseCase.LoginCommand("xavier@foodv.com", "password123")
        );

        assertNotNull(result);
        assertEquals("accessToken", result.accessToken());
        assertEquals("refreshToken", result.refreshToken());
        assertEquals("Bearer", result.tokenType());
        verify(loginAttemptService).resetAttempts("xavier@foodv.com");
    }

    @Test
    @DisplayName("Login fallido — usuario no existe lanza excepción")
    void login_usuario_no_existe_lanza_excepcion() {
        when(loginAttemptService.isBlocked(anyString())).thenReturn(false);
        when(userRepositoryPort.findByEmail("noexiste@foodv.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                loginHandler.execute(new LoginUseCase.LoginCommand("noexiste@foodv.com", "password123"))
        );
    }

    @Test
    @DisplayName("Login fallido — password incorrecto registra intento fallido")
    void login_password_incorrecto_registra_intento() {
        when(loginAttemptService.isBlocked(anyString())).thenReturn(false);
        when(userRepositoryPort.findByEmail("xavier@foodv.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("wrongpassword", "hashedPassword")).thenReturn(false);
        when(loginAttemptService.getRemainingAttempts(anyString())).thenReturn(4);

        assertThrows(IllegalArgumentException.class, () ->
                loginHandler.execute(new LoginUseCase.LoginCommand("xavier@foodv.com", "wrongpassword"))
        );
        verify(loginAttemptService).recordFailedAttempt("xavier@foodv.com");
    }

    @Test
    @DisplayName("Login bloqueado — demasiados intentos fallidos")
    void login_bloqueado_lanza_excepcion() {
        when(loginAttemptService.isBlocked("xavier@foodv.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                loginHandler.execute(new LoginUseCase.LoginCommand("xavier@foodv.com", "password123"))
        );
        assertTrue(ex.getMessage().contains("bloqueada"));
        verifyNoInteractions(userRepositoryPort);
    }

    @Test
    @DisplayName("Login fallido — usuario inactivo lanza excepción")
    void login_usuario_inactivo_lanza_excepcion() {
        User inactiveUser = User.builder()
                .id(2L)
                .email("inactivo@foodv.com")
                .password("hashedPassword")
                .role(UserRole.ESTUDIANTE)
                .activo(false)
                .creadoEn(LocalDateTime.now())
                .build();

        when(loginAttemptService.isBlocked(anyString())).thenReturn(false);
        when(userRepositoryPort.findByEmail("inactivo@foodv.com")).thenReturn(Optional.of(inactiveUser));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                loginHandler.execute(new LoginUseCase.LoginCommand("inactivo@foodv.com", "password123"))
        );
    }
}