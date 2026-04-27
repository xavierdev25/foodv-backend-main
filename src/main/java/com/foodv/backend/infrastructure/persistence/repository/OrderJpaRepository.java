package com.foodv.backend.infrastructure.persistence.repository;

import com.foodv.backend.domain.model.order.OrderStatus;
import com.foodv.backend.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByUserId(Long userId);

    List<OrderEntity> findByStoreId(Long storeId);

    List<OrderEntity> findByStatus(OrderStatus status);

    Page<OrderEntity> findAll(Pageable pageable);

    Page<OrderEntity> findByUserId(Long userId, Pageable pageable);

    Page<OrderEntity> findByStoreId(Long storeId, Pageable pageable);

    List<OrderEntity> findByUserIdAndStatus(Long userId, OrderStatus status);
}
