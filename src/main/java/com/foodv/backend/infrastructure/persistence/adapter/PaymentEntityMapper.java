package com.foodv.backend.infrastructure.persistence.adapter;

import com.foodv.backend.domain.model.payment.Payment;
import com.foodv.backend.infrastructure.persistence.entity.PaymentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentEntityMapper {

    PaymentEntity toEntity(Payment payment);

    Payment toDomain(PaymentEntity entity);
}
