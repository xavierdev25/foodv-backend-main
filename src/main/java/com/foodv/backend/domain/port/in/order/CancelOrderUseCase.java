package com.foodv.backend.domain.port.in.order;

import com.foodv.backend.domain.model.order.Order;

public interface CancelOrderUseCase {

    Order execute(Long orderId);
}
