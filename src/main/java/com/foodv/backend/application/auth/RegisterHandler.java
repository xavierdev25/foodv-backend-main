package com.foodv.backend.application.auth;

import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.domain.model.user.UserRole;
import com.foodv.backend.domain.port.in.auth.RegisterUseCase;
import com.foodv.backend.domain.port.out.BusinessMetricsPort;
import com.foodv.backend.domain.port.out.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class RegisterHandler implements RegisterUseCase {

    /**
     * Roles que pueden crearse desde el endpoint público /auth/register.
     * ADMIN nunca se puede auto-asignar; debe crearse manualmente por otro ADMIN
     * a través de /users (que requiere ROLE_ADMIN en SecurityConfig).
     */
    private static final Set<UserRole> SELF_REGISTRABLE_ROLES = Set.of(
            UserRole.ESTUDIANTE,
            UserRole.REPARTIDOR,
            UserRole.COMERCIO
    );

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final BusinessMetricsPort metricsPort;

    public RegisterHandler(UserRepositoryPort userRepositoryPort,
                           PasswordEncoder passwordEncoder,
                           BusinessMetricsPort metricsPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.metricsPort = metricsPort;
    }

    @Override
    @Transactional
    public User execute(RegisterCommand command) {
        UserRole requestedRole = command.role();
        if (requestedRole == null || !SELF_REGISTRABLE_ROLES.contains(requestedRole)) {
            throw new IllegalArgumentException(
                    "El rol solicitado no permite auto-registro. " +
                    "Roles válidos: ESTUDIANTE, REPARTIDOR, COMERCIO."
            );
        }

        String normalizedEmail = command.email() == null ? "" : command.email().trim().toLowerCase();

        if (userRepositoryPort.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email ya registrado");
        }

        User user = User.builder()
                .nombres(command.nombres() == null ? null : command.nombres().trim())
                .apellidos(command.apellidos() == null ? null : command.apellidos().trim())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(command.password()))
                .telefono(command.telefono() == null ? null : command.telefono().trim())
                .role(requestedRole)
                .activo(true)
                .creadoEn(LocalDateTime.now())
                .preferences(command.preferences() != null ? command.preferences() : List.of())
                .restrictions(command.restrictions() != null ? command.restrictions() : List.of())
                .budgetRange(command.budgetRange() != null ? command.budgetRange() : "MEDIO")
                .cuisineTypes(command.cuisineTypes() != null ? command.cuisineTypes() : List.of())
                .build();

        User saved = userRepositoryPort.save(user);
        metricsPort.recordUserRegistered();
        return saved;
    }
}
