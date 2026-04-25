package com.foodv.backend.domain.port.out;

import com.foodv.backend.domain.model.store.Store;

import java.util.List;
import java.util.Optional;

public interface StoreRepositoryPort {

    Store save(Store store);

    Optional<Store> findById(Long id);

    Optional<Store> findByOwnerId(Long ownerId);

    boolean existsByOwnerId(Long ownerId);

    List<Store> findAll();

    List<Store> findAllActivas();

    void deleteById(Long id);
}
