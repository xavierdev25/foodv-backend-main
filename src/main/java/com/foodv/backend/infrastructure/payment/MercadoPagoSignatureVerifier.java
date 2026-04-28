package com.foodv.backend.infrastructure.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Verifica la firma HMAC SHA-256 de los webhooks de MercadoPago.
 * Reglas:
 *  - Secreto OBLIGATORIO en producción.
 *  - Comparación timing-safe (MessageDigest.isEqual).
 *  - Mensaje completo: "id:{dataId};request-id:{xRequestId};ts:{ts};".
 *  - Tolerancia de timestamp de ±5 minutos para mitigar replay.
 */
@Slf4j
@Component
public class MercadoPagoSignatureVerifier {

    private static final long MAX_TIMESTAMP_SKEW_MILLIS = 5 * 60 * 1000L;

    @Value("${MERCADOPAGO_WEBHOOK_SECRET:}")
    private String webhookSecret;

    public boolean isConfigured() {
        return webhookSecret != null && !webhookSecret.isBlank();
    }

    public boolean verify(String xSignatureHeader, String xRequestId, String dataId) {
        if (!isConfigured()) {
            log.error("MercadoPago webhook secret no configurado. Rechazando webhook.");
            return false;
        }
        if (xSignatureHeader == null || xSignatureHeader.isBlank() || dataId == null) {
            return false;
        }

        Map<String, String> parts = parseSignatureHeader(xSignatureHeader);
        String ts = parts.get("ts");
        String v1 = parts.get("v1");

        if (ts == null || v1 == null) {
            log.warn("X-Signature inválida: faltan campos ts o v1");
            return false;
        }

        long timestampMillis;
        try {
            timestampMillis = Long.parseLong(ts);
        } catch (NumberFormatException e) {
            log.warn("X-Signature ts no numérico");
            return false;
        }
        long now = Instant.now().toEpochMilli();
        if (Math.abs(now - timestampMillis) > MAX_TIMESTAMP_SKEW_MILLIS) {
            log.warn("X-Signature ts fuera de tolerancia ({} vs {})", timestampMillis, now);
            return false;
        }

        String message = "id:" + dataId + ";request-id:" + (xRequestId == null ? "" : xRequestId) + ";ts:" + ts + ";";
        String computed = hmacSha256Hex(webhookSecret, message);

        return constantTimeEquals(computed, v1);
    }

    private Map<String, String> parseSignatureHeader(String header) {
        Map<String, String> result = new HashMap<>();
        for (String part : header.split(",")) {
            String[] kv = part.trim().split("=", 2);
            if (kv.length == 2) {
                result.put(kv[0].trim().toLowerCase(), kv[1].trim());
            }
        }
        return result;
    }

    private String hmacSha256Hex(String secret, String message) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            log.error("Error calculando HMAC: {}", e.getMessage());
            return "";
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        return MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8)
        );
    }
}
