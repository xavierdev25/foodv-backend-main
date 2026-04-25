package com.foodv.backend.application.order;

import com.foodv.backend.domain.model.notification.NotificationEvent;
import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderStatus;
import com.foodv.backend.domain.port.in.order.CancelOrderUseCase;
import com.foodv.backend.domain.port.out.NotificationPort;
import com.foodv.backend.domain.port.out.OrderRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CancelOrderHandler implements CancelOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final NotificationPort notificationPort;

    @Override
    public Order execute(Long orderId) {
        Order existing = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada"));

        if (!existing.getStatus().canTransitionTo(OrderStatus.CANCELADO)) {
            throw new IllegalArgumentException("La orden no puede ser cancelada en su estado actual");
        }

        Order order = Order.builder()
                .id(existing.getId())
                .userId(existing.getUserId())
                .storeId(existing.getStoreId())
                .aulaId(existing.getAulaId())
                .items(existing.getItems())
                .total(existing.getTotal())
                .status(OrderStatus.CANCELADO)
                .notas(existing.getNotas())
                .creadoEn(existing.getCreadoEn())
                .actualizadoEn(LocalDateTime.now())
                .build();

        Order cancelledOrder = orderRepositoryPort.save(order);

        NotificationEvent event = NotificationEvent.builder()
                .type("ORDER_CANCELLED")
                .orderId(cancelledOrder.getId())
                .userId(cancelledOrder.getUserId())
                .storeId(cancelledOrder.getStoreId())
                .message("Tu orden ha sido cancelada")
                .payload(cancelledOrder)
                .timestamp(LocalDateTime.now())
                .build();
        notificationPort.notifyUser(cancelledOrder.getUserId(), event);
        notificationPort.notifyStore(cancelledOrder.getStoreId(), event);
        notificationPort.notifyOrderUpdate(cancelledOrder.getId(), event);

        return cancelledOrder;
    }
}
