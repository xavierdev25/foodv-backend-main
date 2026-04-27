package com.foodv.backend.application.auth;

import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.domain.port.in.auth.RegisterUseCase;
import com.foodv.backend.domain.port.out.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RegisterHandler implements RegisterUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    public RegisterHandler(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User execute(RegisterCommand command) {
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
                .preferences(command.preferences())
                .restrictions(command.restrictions())
                .budgetRange(command.budgetRange())
                .cuisineTypes(command.cuisineTypes())
                .build();

        return userRepositoryPort.save(user);
    }
}
