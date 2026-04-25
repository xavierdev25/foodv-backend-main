package com.foodv.backend.application.store;

import com.foodv.backend.domain.port.in.store.DeleteStoreUseCase;
import com.foodv.backend.domain.port.out.StoreRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteStoreHandler implements DeleteStoreUseCase {

    private final StoreRepositoryPort storeRepositoryPort;

    @Override
    public void execute(Long id) {
        storeRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tienda no encontrada"));

        storeRepositoryPort.deleteById(id);
    }
}
