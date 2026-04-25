package com.foodv.backend.infrastructure.web.mapper;

import com.foodv.backend.domain.model.payment.Payment;
import com.foodv.backend.domain.port.in.payment.CreatePaymentUseCase;
import com.foodv.backend.infrastructure.web.dto.payment.CreatePaymentRequest;
import com.foodv.backend.infrastructure.web.dto.payment.PaymentResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentWebMapper {

    PaymentResponse toResponse(Payment payment);

    CreatePaymentUseCase.CreatePaymentCommand toCommand(CreatePaymentRequest request);
}
