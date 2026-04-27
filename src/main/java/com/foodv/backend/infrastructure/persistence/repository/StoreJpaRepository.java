package com.foodv.backend.infrastructure.persistence.repository;

import com.foodv.backend.infrastructure.persistence.entity.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreJpaRepository extends JpaRepository<StoreEntity, Long> {

    List<StoreEntity> findAllByDeletedAtIsNull();
    Optional<StoreEntity> findByIdAndDeletedAtIsNull(Long id);
    Optional<StoreEntity> findByOwnerIdAndDeletedAtIsNull(Long ownerId);

    Optional<StoreEntity> findByOwnerId(Long ownerId);

    boolean existsByOwnerId(Long ownerId);

    List<StoreEntity> findByActivoTrue();
}
