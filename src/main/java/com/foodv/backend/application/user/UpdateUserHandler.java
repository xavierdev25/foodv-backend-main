package com.foodv.backend.application.user;

import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.domain.port.in.user.UpdateUserUseCase;
import com.foodv.backend.domain.port.out.UserRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UpdateUserHandler implements UpdateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public UpdateUserHandler(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User execute(Long id, UpdateUserCommand command) {
        User existing = userRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        User user = User.builder()
                .id(existing.getId())
                .nombres(command.nombres())
                .apellidos(command.apellidos())
                .email(existing.getEmail())
                .password(existing.getPassword())
                .telefono(command.telefono())
                .role(existing.getRole())
                .activo(existing.isActivo())
                .creadoEn(existing.getCreadoEn())
                .actualizadoEn(LocalDateTime.now())
                .build();

        return userRepositoryPort.save(user);
    }
}
