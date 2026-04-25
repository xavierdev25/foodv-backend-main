package com.foodv.backend.application.order;

import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderStatus;
import com.foodv.backend.domain.port.in.order.UpdateOrderStatusUseCase;
import com.foodv.backend.domain.port.out.OrderRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpdateOrderStatusHandler implements UpdateOrderStatusUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    @Override
    public Order execute(Long orderId, OrderStatus newStatus) {
        Order existing = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada"));

        if (!existing.getStatus().canTransitionTo(newStatus)) {
            throw new IllegalArgumentException("Transición de estado inválida: " + existing.getStatus() + " -> " + newStatus);
        }

        Order order = Order.builder()
                .id(existing.getId())
                .userId(existing.getUserId())
                .storeId(existing.getStoreId())
                .aulaId(existing.getAulaId())
                .items(existing.getItems())
                .total(existing.getTotal())
                .status(newStatus)
                .notas(existing.getNotas())
                .creadoEn(existing.getCreadoEn())
                .actualizadoEn(LocalDateTime.now())
                .build();

        return orderRepositoryPort.save(order);
    }
}
