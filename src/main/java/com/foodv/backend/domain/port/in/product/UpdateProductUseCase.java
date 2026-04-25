package com.foodv.backend.domain.port.in.product;

import com.foodv.backend.domain.model.product.Product;
import com.foodv.backend.domain.model.product.ProductCategory;

import java.math.BigDecimal;

public interface UpdateProductUseCase {

    record UpdateProductCommand(String nombre, String descripcion, BigDecimal precio, Integer stock, ProductCategory categoria, boolean disponible) {}

    Product execute(Long id, UpdateProductCommand command);
}
