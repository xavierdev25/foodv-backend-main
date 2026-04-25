package com.foodv.backend.application.store;

import com.foodv.backend.domain.model.store.Store;
import com.foodv.backend.domain.port.in.store.UpdateStoreUseCase;
import com.foodv.backend.domain.port.out.StoreRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateStoreHandler implements UpdateStoreUseCase {

    private final StoreRepositoryPort storeRepositoryPort;

    @Override
    public Store execute(Long id, UpdateStoreCommand command) {
        Store existing = storeRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tienda no encontrada"));

        Store store = Store.builder()
                .id(existing.getId())
                .nombre(command.nombre())
                .descripcion(command.descripcion())
                .telefono(command.telefono())
                .imagenUrl(existing.getImagenUrl())
                .ownerId(existing.getOwnerId())
                .ownerRole(existing.getOwnerRole())
                .activo(existing.isActivo())
                .creadoEn(existing.getCreadoEn())
                .build();

        return storeRepositoryPort.save(store);
    }
}
