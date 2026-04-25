package com.foodv.backend.domain.model.order;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class OrderItem {

    private Long id;
    private Long productId;
    private String productNombre;
    private BigDecimal productPrecio;
    private Integer cantidad;
    private BigDecimal subtotal;
}
