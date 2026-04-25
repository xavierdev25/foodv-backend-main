package com.foodv.backend.infrastructure.persistence.repository;

import com.foodv.backend.domain.model.product.ProductCategory;
import com.foodv.backend.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByStoreId(Long storeId);

    List<ProductEntity> findByStoreIdAndActivoTrue(Long storeId);

    List<ProductEntity> findByCategoria(ProductCategory categoria);
}
