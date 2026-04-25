package com.foodv.backend.domain.port.in.order;

import com.foodv.backend.domain.model.order.Order;

import java.util.List;

public interface CreateOrderUseCase {

    record OrderItemCommand(Long productId, Integer cantidad) {}

    record CreateOrderCommand(Long userId, Long storeId, Long aulaId, List<OrderItemCommand> items, String notas) {}

    Order execute(CreateOrderCommand command);
}
