package com.foodv.backend.domain.port.in.store;

import com.foodv.backend.domain.model.store.Store;

public interface UpdateStoreUseCase {

    record UpdateStoreCommand(String nombre, String descripcion, String telefono) {}

    Store execute(Long id, UpdateStoreCommand command);
}
