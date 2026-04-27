package com.foodv.backend.application.order;

import com.foodv.backend.domain.model.notification.NotificationEvent;
import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderStatus;
import com.foodv.backend.domain.port.in.order.CancelOrderUseCase;
import com.foodv.backend.domain.port.out.NotificationPort;
import com.foodv.backend.domain.port.out.OrderRepositoryPort;
import com.foodv.backend.domain.port.out.notification.PushNotificationPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CancelOrderHandler implements CancelOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final NotificationPort notificationPort;
    private final PushNotificationPort pushNotificationPort;

    @Override
    public Order execute(CancelOrderCommand command) {
        Order existing = orderRepositoryPort.findById(command.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada"));

        if (!existing.getStatus().canTransitionTo(OrderStatus.CANCELADO)) {
            throw new IllegalArgumentException(
                    "La orden no puede cancelarse en estado: " + existing.getStatus().enEspanol());
        }

        Order order = Order.builder()
                .id(existing.getId())
                .userId(existing.getUserId())
                .storeId(existing.getStoreId())
                .aulaId(existing.getAulaId())
                .repartidorId(existing.getRepartidorId())
                .items(existing.getItems())
                .total(existing.getTotal())
                .propina(existing.getPropina())
                .tarifaServicio(existing.getTarifaServicio())
                .comisionFoodv(existing.getComisionFoodv())
                .status(OrderStatus.CANCELADO)
                .notas(existing.getNotas())
                .motivoCancelacion(command.motivoCancelacion())
                .canceladoPor(command.canceladoPor())
                .codigoConfirmacion(existing.getCodigoConfirmacion())
                .fotoEntregaUrl(existing.getFotoEntregaUrl())
                .creadoEn(existing.getCreadoEn())
                .actualizadoEn(LocalDateTime.now())
                .build();

        Order cancelled = orderRepositoryPort.save(order);

        NotificationEvent event = NotificationEvent.builder()
                .type("ORDER_CANCELLED")
                .orderId(cancelled.getId())
                .userId(cancelled.getUserId())
                .storeId(cancelled.getStoreId())
                .message("Tu orden ha sido cancelada. Motivo: " +
                        (command.motivoCancelacion() != null ? command.motivoCancelacion() : "No especificado"))
                .payload(cancelled)
                .timestamp(LocalDateTime.now())
                .build();

        notificationPort.notifyUser(cancelled.getUserId(), event);
        notificationPort.notifyStore(cancelled.getStoreId(), event);
        notificationPort.notifyOrderUpdate(cancelled.getId(), event);
        pushNotificationPort.sendToUser(
                String.valueOf(cancelled.getUserId()),
                "Orden cancelada",
                "Tu pedido #" + cancelled.getId() + " fue cancelado"
        );

        return cancelled;
    }
}