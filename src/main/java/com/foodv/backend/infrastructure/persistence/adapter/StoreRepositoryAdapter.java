package com.foodv.backend.infrastructure.persistence.adapter;

import com.foodv.backend.domain.model.store.Store;
import com.foodv.backend.domain.port.out.StoreRepositoryPort;
import com.foodv.backend.infrastructure.persistence.repository.StoreJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StoreRepositoryAdapter implements StoreRepositoryPort {

    private final StoreJpaRepository jpaRepository;
    private final StoreEntityMapper mapper;

    @Override
    public Store save(Store store) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(store)));
    }

    @Override
    public Optional<Store> findById(Long id) {
        return jpaRepository.findByIdAndDeletedAtIsNull(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Store> findByOwnerId(Long ownerId) {
        return jpaRepository.findByOwnerId(ownerId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByOwnerId(Long ownerId) {
        return jpaRepository.existsByOwnerId(ownerId);
    }

    @Override
    public List<Store> findAll() {
        return jpaRepository.findAllByDeletedAtIsNull().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Store> findAllActivas() {
        return jpaRepository.findByActivoTrue().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.findByIdAndDeletedAtIsNull(id).ifPresent(entity -> {
            entity.setDeletedAt(LocalDateTime.now());
            jpaRepository.save(entity);
        });
    }
}
