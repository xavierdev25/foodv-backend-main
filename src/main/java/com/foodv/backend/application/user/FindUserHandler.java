package com.foodv.backend.application.user;

import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.domain.port.in.user.FindUserUseCase;
import com.foodv.backend.domain.port.out.UserRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindUserHandler implements FindUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public FindUserHandler(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User findById(Long id) {
        return userRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    @Override
    public User findByEmail(String email) {
        return userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    @Override
    public List<User> findAll() {
        return userRepositoryPort.findAll();
    }
}
