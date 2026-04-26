package com.foodv.backend.domain.port.out;

import com.foodv.backend.domain.model.product.Product;
import com.foodv.backend.domain.model.product.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {

    Product save(Product product);

    Optional<Product> findById(Long id);

    List<Product> findByStoreId(Long storeId);

    List<Product> findByStoreIdAndActivoTrue(Long storeId);

    List<Product> findByCategoria(ProductCategory categoria);

    List<Product> findAll();

    void deleteById(Long id);

    Page<Product> findAllPaginated(Pageable pageable);

    Page<Product> findByStoreIdPaginated(Long storeId, Pageable pageable);
}
