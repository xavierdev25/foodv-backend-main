package com.foodv.backend.application.user;

import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.domain.port.in.user.CreateUserUseCase;
import com.foodv.backend.domain.port.out.BusinessMetricsPort;
import com.foodv.backend.domain.port.out.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CreateUserHandler implements CreateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final BusinessMetricsPort metricsPort;

    public CreateUserHandler(UserRepositoryPort userRepositoryPort,
                             PasswordEncoder passwordEncoder,
                             BusinessMetricsPort metricsPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.metricsPort = metricsPort;
    }

    @Override
    @Transactional
    public User execute(CreateUserCommand command) {
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
                .role(command.role())
                .activo(true)
                .creadoEn(LocalDateTime.now())
                .preferences(command.preferences() != null ? command.preferences() : List.of())
                .restrictions(command.restrictions() != null ? command.restrictions() : List.of())
                .budgetRange(command.budgetRange() != null ? command.budgetRange() : "MEDIO")
                .cuisineTypes(command.cuisineTypes() != null ? command.cuisineTypes() : List.of())
                .build();

        User savedUser = userRepositoryPort.save(user);
        metricsPort.recordUserRegistered();
        return savedUser;
    }
}
