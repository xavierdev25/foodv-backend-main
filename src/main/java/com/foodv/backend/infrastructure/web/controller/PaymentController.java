package com.foodv.backend.infrastructure.web.controller;

import com.foodv.backend.domain.model.payment.Payment;
import com.foodv.backend.domain.port.in.payment.CreatePaymentUseCase;
import com.foodv.backend.domain.port.in.payment.FindPaymentUseCase;
import com.foodv.backend.domain.port.in.payment.ProcessWebhookUseCase;
import com.foodv.backend.infrastructure.web.dto.payment.CreatePaymentRequest;
import com.foodv.backend.infrastructure.web.dto.payment.PaymentResponse;
import com.foodv.backend.infrastructure.web.mapper.PaymentWebMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final CreatePaymentUseCase createPaymentUseCase;
    private final FindPaymentUseCase findPaymentUseCase;
    private final ProcessWebhookUseCase processWebhookUseCase;
    private final PaymentWebMapper mapper;

    @Value("${MERCADOPAGO_WEBHOOK_SECRET:}")
    private String webhookSecret;

    public PaymentController(CreatePaymentUseCase createPaymentUseCase,
                             FindPaymentUseCase findPaymentUseCase,
                             ProcessWebhookUseCase processWebhookUseCase,
                             PaymentWebMapper mapper) {
        this.createPaymentUseCase = createPaymentUseCase;
        this.findPaymentUseCase = findPaymentUseCase;
        this.processWebhookUseCase = processWebhookUseCase;
        this.mapper = mapper;
    }

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

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestBody String rawBody,
            @RequestHeader(value = "x-signature", required = false) String signature,
            @RequestHeader(value = "x-request-id", required = false) String requestId,
            @RequestParam(value = "data.id", required = false) String dataId) {

        // Verificar firma si el secret está configurado
        if (webhookSecret != null && !webhookSecret.isBlank() && signature != null) {
            String expectedSignature = computeHmac(dataId, requestId);
            if (!signature.contains(expectedSignature)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }

        try {
            Map<String, Object> payload = new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(rawBody, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
            Object dataObj = payload.get("data");
            String externalId = null;
            if (dataObj instanceof Map<?, ?> dataMap) {
                Object idObj = dataMap.get("id");
                externalId = idObj != null ? idObj.toString() : null;
            }
            String action = (String) payload.get("action");
            if (externalId != null) {
                processWebhookUseCase.execute(
                    new ProcessWebhookUseCase.WebhookEvent(externalId, action, null)
                );
            }
        } catch (Exception e) {
            // Siempre retornar 200 a MercadoPago aunque falle el procesamiento
        }
        return ResponseEntity.ok().build();
    }

    private String computeHmac(String dataId, String requestId) {
        try {
            String message = "id:" + dataId + ";request-id:" + requestId + ";";
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKey =
                new javax.crypto.spec.SecretKeySpec(
                    webhookSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    "HmacSHA256"
                );
            mac.init(secretKey);
            byte[] hash = mac.doFinal(message.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}

