package com.foodv.backend.application.store;

import com.foodv.backend.domain.model.store.Store;
import com.foodv.backend.domain.port.in.store.UpdateStoreUseCase;
import com.foodv.backend.domain.port.out.StoreRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpdateStoreHandler implements UpdateStoreUseCase {

    private final StoreRepositoryPort storeRepositoryPort;

    @Override
    @Transactional
    public Store execute(Long id, UpdateStoreCommand command) {
        Store existing = storeRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tienda no encontrada"));

        Store store = Store.builder()
                .id(existing.getId())
                .nombre(command.nombre() != null ? command.nombre().trim() : existing.getNombre())
                .descripcion(command.descripcion() != null ? command.descripcion().trim() : existing.getDescripcion())
                .telefono(command.telefono() != null ? command.telefono().trim() : existing.getTelefono())
                .imagenUrl(existing.getImagenUrl())
                .ownerId(existing.getOwnerId())
                .ownerRole(existing.getOwnerRole())
                .activo(existing.isActivo())
                .creadoEn(existing.getCreadoEn())
                .actualizadoEn(LocalDateTime.now())
                .build();

        return storeRepositoryPort.save(store);
    }
}
