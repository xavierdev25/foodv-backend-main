package com.foodv.backend.application.user;

import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.domain.port.in.user.UpdateUserUseCase;
import com.foodv.backend.domain.port.out.UserRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UpdateUserHandler implements UpdateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public UpdateUserHandler(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    @Transactional
    public User execute(Long id, UpdateUserCommand command) {
        if (id == null) throw new IllegalArgumentException("Id requerido");

        User existing = userRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        User user = User.builder()
                .id(existing.getId())
                .nombres(command.nombres() != null ? command.nombres().trim() : existing.getNombres())
                .apellidos(command.apellidos() != null ? command.apellidos().trim() : existing.getApellidos())
                .email(existing.getEmail())
                .password(existing.getPassword())
                .telefono(command.telefono() != null ? command.telefono().trim() : existing.getTelefono())
                .role(existing.getRole())
                .activo(existing.isActivo())
                .creadoEn(existing.getCreadoEn())
                .actualizadoEn(LocalDateTime.now())
                .preferences(existing.getPreferences())
                .restrictions(existing.getRestrictions())
                .budgetRange(existing.getBudgetRange())
                .cuisineTypes(existing.getCuisineTypes())
                .esRepartidor(existing.getEsRepartidor())
                .campusId(existing.getCampusId())
                .build();

        return userRepositoryPort.save(user);
    }
}
