package com.foodv.backend.infrastructure.web.mapper;

import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderItem;
import com.foodv.backend.domain.port.in.order.CreateOrderUseCase;
import com.foodv.backend.infrastructure.web.dto.order.CreateOrderRequest;
import com.foodv.backend.infrastructure.web.dto.order.OrderItemRequest;
import com.foodv.backend.infrastructure.web.dto.order.OrderItemResponse;
import com.foodv.backend.infrastructure.web.dto.order.OrderResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderWebMapper {

    OrderResponse toResponse(Order order);

    OrderItemResponse toItemResponse(OrderItem item);

    CreateOrderUseCase.CreateOrderCommand toCommand(CreateOrderRequest request);

    CreateOrderUseCase.OrderItemCommand toItemCommand(OrderItemRequest request);
}
