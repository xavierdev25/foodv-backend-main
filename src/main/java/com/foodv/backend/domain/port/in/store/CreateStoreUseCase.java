package com.foodv.backend.domain.port.in.store;

import com.foodv.backend.domain.model.store.Store;

public interface CreateStoreUseCase {

    record CreateStoreCommand(String nombre, String descripcion, String telefono, Long ownerId) {}

    Store execute(CreateStoreCommand command);
}
