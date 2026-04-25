package com.foodv.backend.infrastructure.web.dto.payment;

import com.foodv.backend.domain.model.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long orderId,
        Long userId,
        BigDecimal amount,
        PaymentStatus status,
        String externalId,
        String paymentUrl,
        String failureReason,
        LocalDateTime creadoEn,
        LocalDateTime actualizadoEn
) {}
