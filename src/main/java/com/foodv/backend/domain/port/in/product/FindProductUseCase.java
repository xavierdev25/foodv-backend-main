package com.foodv.backend.domain.port.in.product;

import com.foodv.backend.domain.model.product.Product;
import com.foodv.backend.domain.model.product.ProductCategory;

import java.util.List;

public interface FindProductUseCase {

    Product findById(Long id);

    List<Product> findByStoreId(Long storeId);

    List<Product> findByStoreIdActivos(Long storeId);

    List<Product> findByCategoria(ProductCategory categoria);

    List<Product> findAll();
}
