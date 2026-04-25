package com.foodv.backend.application.order;

import com.foodv.backend.domain.model.notification.NotificationEvent;
import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderStatus;
import com.foodv.backend.domain.port.in.order.UpdateOrderStatusUseCase;
import com.foodv.backend.domain.port.out.NotificationPort;
import com.foodv.backend.domain.port.out.OrderRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpdateOrderStatusHandler implements UpdateOrderStatusUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final NotificationPort notificationPort;

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

        Order updatedOrder = orderRepositoryPort.save(order);

        NotificationEvent event = NotificationEvent.builder()
                .type("ORDER_STATUS_CHANGED")
                .orderId(updatedOrder.getId())
                .userId(updatedOrder.getUserId())
                .storeId(updatedOrder.getStoreId())
                .message("Tu orden cambió de estado a: " + newStatus.name())
                .payload(updatedOrder)
                .timestamp(LocalDateTime.now())
                .build();
        notificationPort.notifyUser(updatedOrder.getUserId(), event);
        notificationPort.notifyStore(updatedOrder.getStoreId(), event);
        notificationPort.notifyOrderUpdate(updatedOrder.getId(), event);

        return updatedOrder;
    }
}
