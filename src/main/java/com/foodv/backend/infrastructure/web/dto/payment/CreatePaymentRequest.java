package com.foodv.backend.infrastructure.web.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * El cliente sólo puede indicar la orden a pagar.
 * El monto, descripción y usuario se derivan del servidor para evitar manipulación.
 */
public record CreatePaymentRequest(
        @NotNull(message = "orderId es obligatorio")
        @Positive(message = "orderId debe ser positivo")
        Long orderId
) {}
