package com.foodv.backend.infrastructure.payment;

import com.foodv.backend.domain.model.payment.PaymentStatus;
import com.foodv.backend.domain.port.out.PaymentGatewayPort;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MercadoPagoAdapter implements PaymentGatewayPort {

    private final MercadoPagoConfiguration mercadoPagoConfig;

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        try {
            PreferenceClient preferenceClient = new PreferenceClient();

            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .title(request.description())
                    .quantity(1)
                    .unitPrice(request.amount())
                    .build();

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(request.notificationUrl())
                    .failure(request.notificationUrl())
                    .pending(request.notificationUrl())
                    .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(List.of(item))
                    .backUrls(backUrls)
                    .notificationUrl(request.notificationUrl())
                    .externalReference(request.orderId().toString())
                    .build();

            Preference preference = preferenceClient.create(preferenceRequest);

            return new PaymentResponse(preference.getId(), preference.getInitPoint(), PaymentStatus.PENDIENTE);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear pago en MercadoPago: " + e.getMessage());
        }
    }

    @Override
    public PaymentStatus getPaymentStatus(String externalId) {
        try {
            com.mercadopago.client.payment.PaymentClient paymentClient = new com.mercadopago.client.payment.PaymentClient();
            com.mercadopago.resources.payment.Payment payment = paymentClient.get(Long.parseLong(externalId));

            return switch (payment.getStatus()) {
                case "approved" -> PaymentStatus.APROBADO;
                case "rejected" -> PaymentStatus.RECHAZADO;
                case "cancelled" -> PaymentStatus.CANCELADO;
                default -> PaymentStatus.PENDIENTE;
            };
        } catch (Exception e) {
            return PaymentStatus.PENDIENTE;
        }
    }

    @Override
    public boolean refundPayment(String externalId) {
        try {
            com.mercadopago.client.payment.PaymentRefundClient refundClient = new com.mercadopago.client.payment.PaymentRefundClient();
            refundClient.refund(Long.parseLong(externalId));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
