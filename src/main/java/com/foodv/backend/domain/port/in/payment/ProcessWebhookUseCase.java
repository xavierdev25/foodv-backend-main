package com.foodv.backend.domain.port.in.payment;

public interface ProcessWebhookUseCase {

    record WebhookEvent(String externalId, String action, String status) {}

    void execute(WebhookEvent event);
}
