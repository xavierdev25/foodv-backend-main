package com.foodv.backend.infrastructure.web.dto.order;

import com.foodv.backend.domain.model.order.OrderStatus;
import java.time.LocalDateTime;

public record OrderStatusHistoryResponse(
        Long id,
        Long orderId,
        OrderStatus status,
        Long changedBy,
        String notas,
        LocalDateTime creadoEn
) {}