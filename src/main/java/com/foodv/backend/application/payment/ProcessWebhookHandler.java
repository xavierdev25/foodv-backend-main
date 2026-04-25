package com.foodv.backend.application.payment;

import com.foodv.backend.domain.model.notification.NotificationEvent;
import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.payment.Payment;
import com.foodv.backend.domain.model.payment.PaymentStatus;
import com.foodv.backend.domain.port.in.payment.ProcessWebhookUseCase;
import com.foodv.backend.domain.port.out.NotificationPort;
import com.foodv.backend.domain.port.out.OrderRepositoryPort;
import com.foodv.backend.domain.port.out.PaymentGatewayPort;
import com.foodv.backend.domain.port.out.PaymentRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProcessWebhookHandler implements ProcessWebhookUseCase {

    private final PaymentRepositoryPort paymentRepositoryPort;
    private final PaymentGatewayPort paymentGatewayPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final NotificationPort notificationPort;

    @Override
    public void execute(WebhookEvent event) {
        Optional<Payment> optionalPayment = paymentRepositoryPort.findByExternalId(event.externalId());

        if (optionalPayment.isEmpty()) {
            return;
        }

        Payment existing = optionalPayment.get();
        PaymentStatus newStatus = paymentGatewayPort.getPaymentStatus(event.externalId());

        Payment updatedPayment = Payment.builder()
                .id(existing.getId())
                .orderId(existing.getOrderId())
                .userId(existing.getUserId())
                .amount(existing.getAmount())
                .status(newStatus)
                .externalId(existing.getExternalId())
                .paymentUrl(existing.getPaymentUrl())
                .failureReason(existing.getFailureReason())
                .creadoEn(existing.getCreadoEn())
                .actualizadoEn(LocalDateTime.now())
                .build();

        paymentRepositoryPort.save(updatedPayment);

        if (newStatus == PaymentStatus.APROBADO) {
            Optional<Order> optionalOrder = orderRepositoryPort.findById(existing.getOrderId());
            optionalOrder.ifPresent(order -> {
                NotificationEvent notificationEvent = NotificationEvent.builder()
                        .type("PAYMENT_APPROVED")
                        .orderId(order.getId())
                        .userId(order.getUserId())
                        .storeId(order.getStoreId())
                        .message("Tu pago ha sido aprobado")
                        .payload(updatedPayment)
                        .timestamp(LocalDateTime.now())
                        .build();
                notificationPort.notifyUser(order.getUserId(), notificationEvent);
            });
        }

        if (newStatus == PaymentStatus.RECHAZADO) {
            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .type("PAYMENT_REJECTED")
                    .orderId(existing.getOrderId())
                    .userId(existing.getUserId())
                    .message("Tu pago ha sido rechazado")
                    .payload(updatedPayment)
                    .timestamp(LocalDateTime.now())
                    .build();
            notificationPort.notifyUser(existing.getUserId(), notificationEvent);
        }
    }
}
