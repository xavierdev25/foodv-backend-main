package com.foodv.backend.infrastructure.web.dto.order;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long productId,
        String productNombre,
        BigDecimal productPrecio,
        Integer cantidad,
        BigDecimal subtotal
) {}
