package com.foodv.backend.infrastructure.persistence.adapter;

import com.foodv.backend.domain.model.product.Product;
import com.foodv.backend.domain.model.product.ProductCategory;
import com.foodv.backend.domain.port.out.ProductRepositoryPort;
import com.foodv.backend.infrastructure.persistence.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository jpaRepository;
    private final ProductEntityMapper mapper;

    @Override
    public Page<Product> search(String nombre, ProductCategory categoria, Long storeId,
                                BigDecimal precioMin, BigDecimal precioMax,
                                Boolean disponible, Pageable pageable) {
        return jpaRepository.search(nombre, categoria, storeId, precioMin, precioMax, disponible, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Product save(Product product) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(product)));
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findByIdAndDeletedAtIsNull(id).map(mapper::toDomain);
    }

    @Override
    public List<Product> findByStoreId(Long storeId) {
        return jpaRepository.findByStoreIdAndDeletedAtIsNull(storeId).stream().map(mapper::toDomain).toList();
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
        return jpaRepository.findAllByDeletedAtIsNull().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.findByIdAndDeletedAtIsNull(id).ifPresent(entity -> {
            entity.setDeletedAt(LocalDateTime.now());
            jpaRepository.save(entity);
        });
    }

    @Override
    public Page<Product> findAllPaginated(Pageable pageable) {
        return jpaRepository.findAllByDeletedAtIsNull(pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Product> findByStoreIdPaginated(Long storeId, Pageable pageable) {
        return jpaRepository.findByStoreId(storeId, pageable).map(mapper::toDomain);
    }
}
