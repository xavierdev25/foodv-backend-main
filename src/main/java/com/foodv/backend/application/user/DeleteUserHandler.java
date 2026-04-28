package com.foodv.backend.application.user;

import com.foodv.backend.domain.port.in.user.DeleteUserUseCase;
import com.foodv.backend.domain.port.out.RefreshTokenStorePort;
import com.foodv.backend.domain.port.out.TokenBlacklistPort;
import com.foodv.backend.domain.port.out.UserRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteUserHandler implements DeleteUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final RefreshTokenStorePort refreshTokenStorePort;
    private final TokenBlacklistPort tokenBlacklistPort;

    public DeleteUserHandler(UserRepositoryPort userRepositoryPort,
                             RefreshTokenStorePort refreshTokenStorePort,
                             TokenBlacklistPort tokenBlacklistPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.refreshTokenStorePort = refreshTokenStorePort;
        this.tokenBlacklistPort = tokenBlacklistPort;
    }

    @Override
    @Transactional
    public void execute(Long id) {
        userRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        refreshTokenStorePort.revokeAllByUserId(id);
        tokenBlacklistPort.invalidateAllSessionsBefore(id, System.currentTimeMillis());
        userRepositoryPort.deleteById(id);
    }
}
