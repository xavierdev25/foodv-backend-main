package com.foodv.backend.infrastructure.persistence.repository;

import com.foodv.backend.domain.model.product.ProductCategory;
import com.foodv.backend.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByStoreId(Long storeId);
    List<ProductEntity> findByStoreIdAndActivoTrue(Long storeId);
    List<ProductEntity> findByCategoria(ProductCategory categoria);
    Page<ProductEntity> findAll(Pageable pageable);
    Page<ProductEntity> findByStoreId(Long storeId, Pageable pageable);
    List<ProductEntity> findAllByDeletedAtIsNull();
    List<ProductEntity> findByStoreIdAndDeletedAtIsNull(Long storeId);
    Optional<ProductEntity> findByIdAndDeletedAtIsNull(Long id);
    Page<ProductEntity> findAllByDeletedAtIsNull(Pageable pageable);
    Page<ProductEntity> findByCategoriaAndDeletedAtIsNull(ProductCategory categoria, Pageable pageable);


    @Query("""
    SELECT p FROM ProductEntity p
    WHERE p.activo = true
    AND p.deletedAt IS NULL
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

    /**
     * Decrementa stock atómicamente si hay suficiente.
     * Si cantidad es negativa, devuelve stock (útil al cancelar órdenes).
     * @return número de filas actualizadas (1 si éxito, 0 si no se cumple la condición).
     */
    @Modifying
    @Query("""
        UPDATE ProductEntity p
        SET p.stock = p.stock - :cantidad,
            p.disponible = CASE WHEN (p.stock - :cantidad) > 0 THEN true ELSE false END
        WHERE p.id = :id
          AND p.deletedAt IS NULL
          AND (:cantidad <= 0 OR p.stock >= :cantidad)
    """)
    int decrementStock(@Param("id") Long id, @Param("cantidad") int cantidad);

    @Modifying
    @Query("""
    UPDATE ProductEntity p
    SET p.stock = p.stock + :cantidad,
        p.disponible = CASE WHEN (p.stock + :cantidad) > 0 THEN true ELSE false END
    WHERE p.id = :id
      AND p.deletedAt IS NULL
""")
    int incrementStock(@Param("id") Long id, @Param("cantidad") int cantidad);
}
