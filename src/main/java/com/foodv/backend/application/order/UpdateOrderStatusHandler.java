package com.foodv.backend.application.order;

import com.foodv.backend.domain.model.notification.NotificationEvent;
import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderStatus;
import com.foodv.backend.domain.port.in.order.UpdateOrderStatusUseCase;
import com.foodv.backend.domain.port.out.BusinessMetricsPort;
import com.foodv.backend.domain.port.out.NotificationPort;
import com.foodv.backend.domain.port.out.OrderHistoryPort;
import com.foodv.backend.domain.port.out.OrderRepositoryPort;
import com.foodv.backend.domain.port.out.notification.PushNotificationPort;
import com.foodv.backend.domain.service.OrderDomainService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpdateOrderStatusHandler implements UpdateOrderStatusUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final NotificationPort notificationPort;
    private final OrderHistoryPort orderHistoryPort;
    private final BusinessMetricsPort metricsPort;
    private final PushNotificationPort pushNotificationPort;
    private final ApplicationEventPublisher eventPublisher;

    private static final OrderDomainService DOMAIN = new OrderDomainService();

    @Override
    @Transactional
    public Order execute(Long orderId, OrderStatus newStatus, Long changedBy) {
        Order existing = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada"));

        Order updatedOrderModel = DOMAIN.applyStatusTransition(existing, newStatus);
        Order updatedOrder = orderRepositoryPort.save(updatedOrderModel);

        orderHistoryPort.record(updatedOrder.getId(), newStatus, changedBy,
                "Estado actualizado a " + newStatus.enEspanol());

        if (newStatus == OrderStatus.ENTREGADO) {
            metricsPort.recordOrderCompleted();
        } else if (newStatus == OrderStatus.CANCELADO) {
            metricsPort.recordOrderCancelled();
        }

        // Notificaciones asíncronas: no deben bloquear ni romper el flujo
        eventPublisher.publishEvent(new OrderStatusChangedEvent(
                updatedOrder.getId(),
                updatedOrder.getUserId(),
                updatedOrder.getStoreId(),
                newStatus,
                updatedOrder,
                LocalDateTime.now()
        ));

        // Enviar también de forma síncrona para mantener tests/contratos actuales,
        // pero protegido contra fallos.
        sendSafe(updatedOrder, newStatus);

        return updatedOrder;
    }

    private void sendSafe(Order order, OrderStatus newStatus) {
        try {
            NotificationEvent event = NotificationEvent.builder()
                    .type("ORDER_STATUS_CHANGED")
                    .orderId(order.getId())
                    .userId(order.getUserId())
                    .storeId(order.getStoreId())
                    .message("Tu orden cambió de estado a: " + newStatus.enEspanol())
                    .payload(order)
                    .timestamp(LocalDateTime.now())
                    .build();
            notificationPort.notifyUser(order.getUserId(), event);
            notificationPort.notifyStore(order.getStoreId(), event);
            notificationPort.notifyOrderUpdate(order.getId(), event);

            pushNotificationPort.sendToUser(
                    String.valueOf(order.getUserId()),
                    "Actualización de tu pedido #" + order.getId(),
                    "Tu pedido ahora está: " + newStatus.enEspanol()
            );
        } catch (Exception ignore) {
            // los adapters de notificación deben manejar sus errores; este es defensa en profundidad.
        }
    }

    public record OrderStatusChangedEvent(
            Long orderId,
            Long userId,
            Long storeId,
            OrderStatus newStatus,
            Order order,
            LocalDateTime occurredAt
    ) {}
}
