package com.foodv.backend.domain.model.order;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Order {

    @EqualsAndHashCode.Include
    private Long id;

    private Long userId;
    private Long storeId;
    private Long aulaId;
    private List<OrderItem> items;
    private BigDecimal total;
    private OrderStatus status;
    private String notas;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
