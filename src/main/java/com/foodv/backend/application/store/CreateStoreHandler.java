package com.foodv.backend.application.store;

import com.foodv.backend.domain.model.store.Store;
import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.domain.model.user.UserRole;
import com.foodv.backend.domain.port.in.store.CreateStoreUseCase;
import com.foodv.backend.domain.port.out.StoreRepositoryPort;
import com.foodv.backend.domain.port.out.UserRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateStoreHandler implements CreateStoreUseCase {

    private final StoreRepositoryPort storeRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public Store execute(CreateStoreCommand command) {
        User owner = userRepositoryPort.findById(command.ownerId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (owner.getRole() != UserRole.COMERCIO && owner.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("El usuario debe tener rol COMERCIO o ADMIN para crear una tienda");
        }

        if (storeRepositoryPort.existsByOwnerId(command.ownerId())) {
            throw new IllegalArgumentException("El usuario ya tiene una tienda registrada");
        }

        Store store = Store.builder()
                .nombre(command.nombre())
                .descripcion(command.descripcion())
                .telefono(command.telefono())
                .ownerId(command.ownerId())
                .ownerRole(owner.getRole())
                .activo(true)
                .creadoEn(LocalDateTime.now())
                .build();

        return storeRepositoryPort.save(store);
    }
}
