package com.foodv.backend.infrastructure.web.controller;

import com.foodv.backend.domain.model.payment.Payment;
import com.foodv.backend.domain.port.in.payment.CreatePaymentUseCase;
import com.foodv.backend.domain.port.in.payment.FindPaymentUseCase;
import com.foodv.backend.domain.port.in.payment.ProcessWebhookUseCase;
import com.foodv.backend.infrastructure.web.dto.payment.CreatePaymentRequest;
import com.foodv.backend.infrastructure.web.dto.payment.PaymentResponse;
import com.foodv.backend.infrastructure.web.mapper.PaymentWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final CreatePaymentUseCase createPaymentUseCase;
    private final FindPaymentUseCase findPaymentUseCase;
    private final ProcessWebhookUseCase processWebhookUseCase;
    private final PaymentWebMapper mapper;

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request) {
        Payment payment = createPaymentUseCase.execute(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(payment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> findById(@PathVariable Long id) {
        Payment payment = findPaymentUseCase.findById(id);
        return ResponseEntity.ok(mapper.toResponse(payment));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> findByOrderId(@PathVariable Long orderId) {
        Payment payment = findPaymentUseCase.findByOrderId(orderId);
        return ResponseEntity.ok(mapper.toResponse(payment));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> findByUserId(@PathVariable Long userId) {
        List<PaymentResponse> responses = findPaymentUseCase.findByUserId(userId).stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody Map<String, Object> payload) {
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        String externalId = data != null ? String.valueOf(data.get("id")) : null;
        String action = String.valueOf(payload.get("action"));

        ProcessWebhookUseCase.WebhookEvent event = new ProcessWebhookUseCase.WebhookEvent(
                externalId,
                action,
                null
        );

        processWebhookUseCase.execute(event);

        return ResponseEntity.ok().build();
    }
}
