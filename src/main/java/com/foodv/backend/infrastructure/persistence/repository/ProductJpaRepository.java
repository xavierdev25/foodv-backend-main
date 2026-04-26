package com.foodv.backend.infrastructure.persistence.repository;

import com.foodv.backend.domain.model.product.ProductCategory;
import com.foodv.backend.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByStoreId(Long storeId);
    List<ProductEntity> findByStoreIdAndActivoTrue(Long storeId);
    List<ProductEntity> findByCategoria(ProductCategory categoria);
    Page<ProductEntity> findAll(Pageable pageable);
    Page<ProductEntity> findByStoreId(Long storeId, Pageable pageable);

    @Query("""
    SELECT p FROM ProductEntity p
    WHERE p.activo = true
    AND (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', CAST(:nombre AS string), '%')))
    AND (:categoria IS NULL OR p.categoria = :categoria)
    AND (:storeId IS NULL OR p.storeId = :storeId)
    AND (:precioMin IS NULL OR p.precio >= :precioMin)
    AND (:precioMax IS NULL OR p.precio <= :precioMax)
    AND (:disponible IS NULL OR p.disponible = :disponible)
""")
    Page<ProductEntity> search(
            @Param("nombre") String nombre,
            @Param("categoria") ProductCategory categoria,
            @Param("storeId") Long storeId,
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax,
            @Param("disponible") Boolean disponible,
            Pageable pageable
    );
}