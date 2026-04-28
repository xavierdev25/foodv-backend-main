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
        Long repartidorId,
        List<OrderItemResponse> items,
        BigDecimal total,
        BigDecimal propina,
        BigDecimal tarifaServicio,
        BigDecimal comisionFoodv,
        OrderStatus status,
        String statusDescripcion,
        String notas,
        String motivoCancelacion,
        Long canceladoPor,
        String codigoConfirmacion,
        String fotoEntregaUrl,
        LocalDateTime creadoEn,
        LocalDateTime actualizadoEn
) {}