package com.foodv.backend.domain.port.in.order;

import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderStatus;

public interface UpdateOrderStatusUseCase {
    Order execute(Long orderId, OrderStatus newStatus, Long changedBy);
}