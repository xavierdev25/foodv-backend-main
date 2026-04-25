package com.foodv.backend.infrastructure.persistence.repository;

import com.foodv.backend.domain.model.order.OrderStatus;
import com.foodv.backend.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByUserId(Long userId);

    List<OrderEntity> findByStoreId(Long storeId);

    List<OrderEntity> findByStatus(OrderStatus status);
}
