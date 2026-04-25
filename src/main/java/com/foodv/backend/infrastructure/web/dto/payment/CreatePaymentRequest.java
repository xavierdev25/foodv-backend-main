package com.foodv.backend.infrastructure.web.dto.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreatePaymentRequest(
        @NotNull Long orderId,
        @NotNull Long userId,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank String description
) {}
