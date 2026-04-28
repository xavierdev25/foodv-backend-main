package com.foodv.backend.domain.port.in.order;

import com.foodv.backend.domain.model.order.Order;

public interface CancelOrderUseCase {
    record CancelOrderCommand(Long orderId, Long canceladoPor, String motivoCancelacion) {}
    Order execute(CancelOrderCommand command);
}