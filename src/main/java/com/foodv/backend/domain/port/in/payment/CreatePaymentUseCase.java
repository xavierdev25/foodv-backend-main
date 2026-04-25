package com.foodv.backend.domain.port.in.payment;

import com.foodv.backend.domain.model.payment.Payment;

import java.math.BigDecimal;

public interface CreatePaymentUseCase {

    record CreatePaymentCommand(Long orderId, Long userId, BigDecimal amount, String description) {}

    Payment execute(CreatePaymentCommand command);
}
