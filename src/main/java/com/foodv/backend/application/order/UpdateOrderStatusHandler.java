package com.foodv.backend.application.order;

import com.foodv.backend.domain.model.notification.NotificationEvent;
import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderStatus;
import com.foodv.backend.domain.port.in.order.UpdateOrderStatusUseCase;
import com.foodv.backend.domain.port.out.NotificationPort;
import com.foodv.backend.domain.port.out.OrderRepositoryPort;
import com.foodv.backend.domain.port.out.notification.PushNotificationPort;
import com.foodv.backend.infrastructure.metrics.BusinessMetricsService;
import com.foodv.backend.infrastructure.persistence.entity.OrderStatusHistoryEntity;
import com.foodv.backend.infrastructure.persistence.repository.OrderStatusHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpdateOrderStatusHandler implements UpdateOrderStatusUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final NotificationPort notificationPort;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final BusinessMetricsService metricsService;
    private final PushNotificationPort pushNotificationPort;

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

        orderStatusHistoryRepository.save(OrderStatusHistoryEntity.builder()
                .orderId(order.getId())
                .status(newStatus)
                .changedBy(null)
                .notas("Estado actualizado a " + newStatus.name())
                .creadoEn(java.time.LocalDateTime.now())
                .build());

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

        if (newStatus == OrderStatus.ENTREGADO) {
            metricsService.recordOrderCompleted();
        } else if (newStatus == OrderStatus.CANCELADO) {
            metricsService.recordOrderCancelled();
        }

        String title = "Actualización de tu pedido";
        String body = "Tu pedido ahora está en estado: " + newStatus.name();
        pushNotificationPort.sendToUser(String.valueOf(order.getUserId()), title, body);

        return updatedOrder;
    }
}
