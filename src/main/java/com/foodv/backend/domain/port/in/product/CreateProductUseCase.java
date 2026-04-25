package com.foodv.backend.domain.port.in.product;

import com.foodv.backend.domain.model.product.Product;
import com.foodv.backend.domain.model.product.ProductCategory;

import java.math.BigDecimal;

public interface CreateProductUseCase {

    record CreateProductCommand(String nombre, String descripcion, BigDecimal precio, Integer stock, ProductCategory categoria, Long storeId) {}

    Product execute(CreateProductCommand command);
}
