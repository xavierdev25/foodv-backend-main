package com.foodv.backend.infrastructure.web.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
        @NotNull Long storeId,
        @NotNull Long aulaId,
        @NotEmpty List<@Valid OrderItemRequest> items,
        String notas,
        BigDecimal propina
) {
    public CreateOrderRequest {
        if (propina == null) propina = BigDecimal.ZERO;
    }
}