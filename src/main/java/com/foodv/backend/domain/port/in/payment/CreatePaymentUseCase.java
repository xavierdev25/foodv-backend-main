package com.foodv.backend.domain.port.in.payment;

import com.foodv.backend.domain.model.payment.Payment;

public interface CreatePaymentUseCase {

    /**
     * Comando seguro: el orderId y el email del usuario autenticado son los únicos inputs.
     * El monto, descripción y dueño se derivan en el servidor.
     */
    record CreatePaymentCommand(Long orderId, String requesterEmail) {}

    Payment execute(CreatePaymentCommand command);
}
