package com.foodv.backend.application.order;

import com.foodv.backend.domain.exception.ResourceNotFoundException;
import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderStatus;
import com.foodv.backend.domain.port.in.order.UpdateOrderStatusUseCase;
import com.foodv.backend.domain.port.out.BusinessMetricsPort;
import com.foodv.backend.domain.port.out.OrderHistoryPort;
import com.foodv.backend.domain.port.out.OrderRepositoryPort;
import com.foodv.backend.domain.service.OrderDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpdateOrderStatusHandler implements UpdateOrderStatusUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final OrderHistoryPort orderHistoryPort;
    private final BusinessMetricsPort metricsPort;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderDomainService orderDomainService;

    @Override
    @Transactional
    public Order execute(Long orderId, OrderStatus newStatus, Long changedBy) {
        Order existing = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        Order updatedOrderModel = orderDomainService.applyStatusTransition(existing, newStatus);
        Order updatedOrder = orderRepositoryPort.save(updatedOrderModel);

        orderHistoryPort.record(updatedOrder.getId(), newStatus, changedBy,
                "Estado actualizado a " + newStatus.enEspanol());

        if (newStatus == OrderStatus.ENTREGADO) {
            metricsPort.recordOrderCompleted();
        } else if (newStatus == OrderStatus.CANCELADO) {
            metricsPort.recordOrderCancelled();
        }

        // Only async notifications are published here to avoid duplicate delivery and keep status changes non-blocking.
        eventPublisher.publishEvent(new OrderStatusChangedEvent(
                updatedOrder.getId(),
                updatedOrder.getUserId(),
                updatedOrder.getStoreId(),
                newStatus,
                updatedOrder,
                LocalDateTime.now()
        ));

        return updatedOrder;
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
