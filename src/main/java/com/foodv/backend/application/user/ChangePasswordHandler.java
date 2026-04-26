package com.foodv.backend.application.user;

import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.domain.port.in.user.ChangePasswordUseCase;
import com.foodv.backend.domain.port.out.UserRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChangePasswordHandler implements ChangePasswordUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordHandler(UserRepositoryPort userRepositoryPort,
                                 PasswordEncoder passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void execute(String email, String currentPassword, String newPassword) {
        User user = userRepositoryPort.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        User updated = User.builder()
            .id(user.getId())
            .nombres(user.getNombres())
            .apellidos(user.getApellidos())
            .email(user.getEmail())
            .password(passwordEncoder.encode(newPassword))
            .telefono(user.getTelefono())
            .role(user.getRole())
            .activo(user.isActivo())
            .creadoEn(user.getCreadoEn())
            .actualizadoEn(LocalDateTime.now())
            .build();

        userRepositoryPort.save(updated);
    }
}
