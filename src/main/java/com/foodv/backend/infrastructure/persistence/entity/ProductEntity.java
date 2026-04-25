package com.foodv.backend.infrastructure.persistence.entity;

import com.foodv.backend.domain.model.product.ProductCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory categoria;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(nullable = false)
    private boolean activo;

    @Column(nullable = false)
    private boolean disponible;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;
}
