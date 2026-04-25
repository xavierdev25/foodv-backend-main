package com.foodv.backend.application.product;

import com.foodv.backend.domain.model.product.Product;
import com.foodv.backend.domain.model.product.ProductCategory;
import com.foodv.backend.domain.port.in.product.FindProductUseCase;
import com.foodv.backend.domain.port.out.ProductRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public List<Product> findAll() {
        return productRepositoryPort.findAll();
    }
}
