package com.foodv.backend.application.payment;

import com.foodv.backend.domain.model.payment.Payment;
import com.foodv.backend.domain.model.payment.PaymentStatus;
import com.foodv.backend.domain.port.in.payment.CreatePaymentUseCase;
import com.foodv.backend.domain.port.out.OrderRepositoryPort;
import com.foodv.backend.domain.port.out.PaymentGatewayPort;
import com.foodv.backend.domain.port.out.PaymentRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreatePaymentHandler implements CreatePaymentUseCase {

    private final PaymentRepositoryPort paymentRepositoryPort;
    private final PaymentGatewayPort paymentGatewayPort;
    private final OrderRepositoryPort orderRepositoryPort;

    @Value("${MERCADOPAGO_NOTIFICATION_URL:http://localhost:8080/api/payments/webhook}")
    private String notificationUrl;

    @Override
    public Payment execute(CreatePaymentCommand command) {
        orderRepositoryPort.findById(command.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada"));

        if (paymentRepositoryPort.findByOrderId(command.orderId()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un pago para esta orden");
        }

        PaymentGatewayPort.PaymentRequest request = new PaymentGatewayPort.PaymentRequest(
                command.orderId(),
                command.userId(),
                command.amount(),
                command.description(),
                notificationUrl
        );

        PaymentGatewayPort.PaymentResponse response = paymentGatewayPort.createPayment(request);

        Payment payment = Payment.builder()
                .orderId(command.orderId())
                .userId(command.userId())
                .amount(command.amount())
                .status(PaymentStatus.PENDIENTE)
                .externalId(response.externalId())
                .paymentUrl(response.paymentUrl())
                .creadoEn(LocalDateTime.now())
                .actualizadoEn(LocalDateTime.now())
                .build();

        return paymentRepositoryPort.save(payment);
    }
}
