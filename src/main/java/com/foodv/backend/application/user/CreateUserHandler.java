package com.foodv.backend.application.user;

import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.domain.port.in.user.CreateUserUseCase;
import com.foodv.backend.domain.port.out.UserRepositoryPort;
import com.foodv.backend.infrastructure.metrics.BusinessMetricsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CreateUserHandler implements CreateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final BusinessMetricsService metricsService;

    public CreateUserHandler(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder, BusinessMetricsService metricsService) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.metricsService = metricsService;
    }

    @Override
    public User execute(CreateUserCommand command) {
        if (userRepositoryPort.existsByEmail(command.email())) {
            throw new IllegalArgumentException("Email ya registrado");
        }

        User user = User.builder()
                .nombres(command.nombres())
                .apellidos(command.apellidos())
                .email(command.email())
                .password(passwordEncoder.encode(command.password()))
                .telefono(command.telefono())
                .role(command.role())
                .activo(true)
                .creadoEn(LocalDateTime.now())
                .build();

        User savedUser = userRepositoryPort.save(user);
        metricsService.recordUserRegistered();

        return savedUser;
    }
}
