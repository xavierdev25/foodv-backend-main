package com.foodv.backend.infrastructure.web.dto.order;

import com.foodv.backend.domain.model.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long userId,
        Long storeId,
        Long aulaId,
        List<OrderItemResponse> items,
        BigDecimal total,
        OrderStatus status,
        String notas,
        LocalDateTime creadoEn,
        LocalDateTime actualizadoEn
) {}
