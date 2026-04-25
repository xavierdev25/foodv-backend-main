package com.foodv.backend.domain.port.out;

import com.foodv.backend.domain.model.payment.PaymentStatus;

import java.math.BigDecimal;

public interface PaymentGatewayPort {

    record PaymentRequest(Long orderId, Long userId, BigDecimal amount, String description, String notificationUrl) {}

    record PaymentResponse(String externalId, String paymentUrl, PaymentStatus status) {}

    PaymentResponse createPayment(PaymentRequest request);

    PaymentStatus getPaymentStatus(String externalId);

    boolean refundPayment(String externalId);
}
