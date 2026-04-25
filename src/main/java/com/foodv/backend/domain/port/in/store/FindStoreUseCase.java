package com.foodv.backend.domain.port.in.store;

import com.foodv.backend.domain.model.store.Store;

import java.util.List;

public interface FindStoreUseCase {

    Store findById(Long id);

    Store findByOwnerId(Long ownerId);

    List<Store> findAll();

    List<Store> findAllActivas();
}
