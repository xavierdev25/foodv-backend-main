package com.foodv.backend.infrastructure.web.dto.product;

import com.foodv.backend.domain.model.product.ProductCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Todos los campos son opcionales (semántica PATCH/PUT parcial).
 */
public record UpdateProductRequest(
        @Size(min = 2, max = 100) String nombre,
        @Size(max = 500) String descripcion,
        @DecimalMin("0.01") @DecimalMax("99999.99") BigDecimal precio,
        @Min(0) Integer stock,
        ProductCategory categoria,
        Boolean disponible
) {}
