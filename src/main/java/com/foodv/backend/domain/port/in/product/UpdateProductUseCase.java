package com.foodv.backend.domain.port.in.product;

import com.foodv.backend.domain.model.product.Product;
import com.foodv.backend.domain.model.product.ProductCategory;

import java.math.BigDecimal;

public interface UpdateProductUseCase {

    /**
     * Comando con campos opcionales (semántica PATCH): null = no modificar.
     */
    record UpdateProductCommand(
            String nombre,
            String descripcion,
            BigDecimal precio,
            Integer stock,
            ProductCategory categoria,
            Boolean disponible
    ) {}

    Product execute(Long id, UpdateProductCommand command);
}
