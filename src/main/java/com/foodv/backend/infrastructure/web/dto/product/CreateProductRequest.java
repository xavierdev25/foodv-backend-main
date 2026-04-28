package com.foodv.backend.infrastructure.web.dto.product;

import com.foodv.backend.domain.model.product.ProductCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank(message = "Nombre obligatorio")
        @Size(min = 2, max = 100, message = "Nombre entre 2 y 100 caracteres")
        String nombre,

        @Size(max = 500, message = "Descripción máximo 500 caracteres")
        String descripcion,

        @NotNull(message = "Precio obligatorio")
        @DecimalMin(value = "0.01", message = "Precio mínimo 0.01")
        @DecimalMax(value = "99999.99", message = "Precio máximo 99999.99")
        BigDecimal precio,

        @NotNull(message = "Stock obligatorio")
        @Min(value = 0, message = "Stock no puede ser negativo")
        Integer stock,

        @NotNull(message = "Categoría obligatoria")
        ProductCategory categoria
) {}
