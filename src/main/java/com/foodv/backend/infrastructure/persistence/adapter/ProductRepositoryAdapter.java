package com.foodv.backend.infrastructure.persistence.adapter;

import com.foodv.backend.domain.model.product.Product;
import com.foodv.backend.domain.model.product.ProductCategory;
import com.foodv.backend.domain.port.out.ProductRepositoryPort;
import com.foodv.backend.infrastructure.persistence.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository jpaRepository;
    private final ProductEntityMapper mapper;

    @Override
    public Product save(Product product) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(product)));
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Product> findByStoreId(Long storeId) {
        return jpaRepository.findByStoreId(storeId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Product> findByStoreIdAndActivoTrue(Long storeId) {
        return jpaRepository.findByStoreIdAndActivoTrue(storeId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Product> findByCategoria(ProductCategory categoria) {
        return jpaRepository.findByCategoria(categoria).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Page<Product> findAllPaginated(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Product> findByStoreIdPaginated(Long storeId, Pageable pageable) {
        return jpaRepository.findByStoreId(storeId, pageable).map(mapper::toDomain);
    }
}
