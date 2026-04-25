package com.foodv.backend.domain.port.in.order;

import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderStatus;

import java.util.List;

public interface FindOrderUseCase {

    Order findById(Long id);

    List<Order> findByUserId(Long userId);

    List<Order> findByStoreId(Long storeId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findAll();
}
