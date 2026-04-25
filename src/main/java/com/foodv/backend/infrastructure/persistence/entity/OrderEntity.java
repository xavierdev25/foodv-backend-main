package com.foodv.backend.infrastructure.persistence.entity;

import com.foodv.backend.domain.model.order.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "aula_id", nullable = false)
    private Long aulaId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "order_id", nullable = false)
    private List<OrderItemEntity> items = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private String notas;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;
}
