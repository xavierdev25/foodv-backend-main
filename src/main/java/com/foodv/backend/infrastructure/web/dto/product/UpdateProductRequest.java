package com.foodv.backend.infrastructure.web.dto.product;

import com.foodv.backend.domain.model.product.ProductCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateProductRequest(
        @NotBlank String nombre,
        String descripcion,
        @NotNull @DecimalMin("0.01") BigDecimal precio,
        @NotNull @Min(0) Integer stock,
        @NotNull ProductCategory categoria,
        boolean disponible
) {}
