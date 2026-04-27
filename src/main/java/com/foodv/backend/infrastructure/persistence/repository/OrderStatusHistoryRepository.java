package com.foodv.backend.infrastructure.persistence.repository;

import com.foodv.backend.infrastructure.persistence.entity.OrderStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistoryEntity, Long> {
    List<OrderStatusHistoryEntity> findByOrderIdOrderByCreadoEnAsc(Long orderId);
}