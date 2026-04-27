package com.foodv.backend.infrastructure.persistence.repository;

import com.foodv.backend.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> findAllByDeletedAtIsNull();
    List<UserEntity> findAllByDeletedAtIsNotNull();
    Optional<UserEntity> findByEmailAndDeletedAtIsNull(String email);
    Optional<UserEntity> findByIdAndDeletedAtIsNull(Long id);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
