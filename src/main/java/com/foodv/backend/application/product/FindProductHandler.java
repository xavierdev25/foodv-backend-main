package com.foodv.backend.application.product;

import com.foodv.backend.domain.model.product.Product;
import com.foodv.backend.domain.model.product.ProductCategory;
import com.foodv.backend.domain.port.in.product.FindProductUseCase;
import com.foodv.backend.domain.port.out.ProductRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindProductHandler implements FindProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    @Override
    public Product findById(Long id) {
        return productRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
    }

    @Override
    @Cacheable(value = "products", key = "#storeId")
    public List<Product> findByStoreId(Long storeId) {
        return productRepositoryPort.findByStoreId(storeId);
    }

    @Override
    public List<Product> findByStoreIdActivos(Long storeId) {
        return productRepositoryPort.findByStoreIdAndActivoTrue(storeId);
    }

    @Override
    public List<Product> findByCategoria(ProductCategory categoria) {
        return productRepositoryPort.findByCategoria(categoria);
    }

    @Override
    @Cacheable("products")
    public List<Product> findAll() {
        return productRepositoryPort.findAll();
    }

    @Override
    public Page<Product> findAllPaginated(Pageable pageable) {
        return productRepositoryPort.findAllPaginated(pageable);
    }

    @Override
    public Page<Product> findByStoreIdPaginated(Long storeId, Pageable pageable) {
        return productRepositoryPort.findByStoreIdPaginated(storeId, pageable);
    }

    @Override
    public Page<Product> search(String nombre, ProductCategory categoria, Long storeId,
                                BigDecimal precioMin, BigDecimal precioMax,
                                Boolean disponible, Pageable pageable) {
        return productRepositoryPort.search(nombre, categoria, storeId, precioMin, precioMax, disponible, pageable);
    }
}
