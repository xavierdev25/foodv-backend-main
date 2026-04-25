package com.foodv.backend.infrastructure.web.dto.product;

import com.foodv.backend.domain.model.product.ProductCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Integer stock,
        String imagenUrl,
        ProductCategory categoria,
        Long storeId,
        boolean activo,
        boolean disponible,
        LocalDateTime creadoEn
) {}
