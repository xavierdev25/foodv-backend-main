package com.foodv.backend.application.user;

import com.foodv.backend.domain.port.in.user.DeleteUserUseCase;
import com.foodv.backend.domain.port.out.UserRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DeleteUserHandler implements DeleteUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public DeleteUserHandler(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public void execute(Long id) {
        userRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        userRepositoryPort.deleteById(id);
    }
}
