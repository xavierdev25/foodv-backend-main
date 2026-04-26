package com.foodv.backend.domain.port.in.product;

import com.foodv.backend.domain.model.product.Product;
import com.foodv.backend.domain.model.product.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;

import java.util.List;

public interface FindProductUseCase {

    Product findById(Long id);

    List<Product> findByStoreId(Long storeId);

    List<Product> findByStoreIdActivos(Long storeId);

    List<Product> findByCategoria(ProductCategory categoria);

    List<Product> findAll();

    Page<Product> findAllPaginated(Pageable pageable);

    Page<Product> findByStoreIdPaginated(Long storeId, Pageable pageable);

    Page<Product> search(String nombre, ProductCategory categoria, Long storeId,
                         BigDecimal precioMin, BigDecimal precioMax,
                         Boolean disponible, Pageable pageable);
}
