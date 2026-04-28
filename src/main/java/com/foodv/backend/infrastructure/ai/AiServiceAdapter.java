package com.foodv.backend.infrastructure.ai;

import com.foodv.backend.domain.model.ai.AiRecommendationRequest;
import com.foodv.backend.domain.model.ai.AiRecommendationResponse;
import com.foodv.backend.domain.port.out.AiRecommendationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Llama al microservicio de IA con timeouts (configurados en {@code RestClientConfig}) y un
 * circuit breaker simple basado en contador de fallos consecutivos.
 * Si la IA no responde, retorna un fallback heurístico (top productos por categoría/precio).
 */
@Slf4j
@Component
public class AiServiceAdapter implements AiRecommendationPort {

    private static final int FAILURE_THRESHOLD = 5;
    private static final long OPEN_DURATION_MILLIS = 30_000L;

    private final RestClient restClient;
    private final String aiServiceUrl;
    private final String aiSecretKey;

    private final AtomicInteger consecutiveFailures = new AtomicInteger();
    private final AtomicLong openUntil = new AtomicLong();

    public AiServiceAdapter(RestClient restClient,
                            @Value("${ai.service.url:http://localhost:8001}") String aiServiceUrl,
                            @Value("${ai.service.secret-key:}") String aiSecretKey) {
        this.restClient = restClient;
        this.aiServiceUrl = aiServiceUrl;
        this.aiSecretKey = aiSecretKey;
    }

    @Override
    public AiRecommendationResponse getRecommendations(AiRecommendationRequest request) {
        if (System.currentTimeMillis() < openUntil.get()) {
            log.warn("Circuit breaker IA abierto; usando fallback");
            return fallback(request);
        }
        try {
            List<Map<String, Object>> productsPayload = request.getAvailableProducts().stream()
                    .map(p -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", p.id());
                        map.put("nombre", p.nombre());
                        map.put("precio", p.precio());
                        map.put("categoria", p.categoria());
                        return map;
                    })
                    .toList();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("user_id", request.getUserId());
            requestBody.put("restrictions", request.getRestrictions());
            requestBody.put("preferences", request.getPreferences());
            requestBody.put("available_products", productsPayload);
            requestBody.put("max_recommendations", request.getMaxRecommendations());

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri(aiServiceUrl + "/api/ai/recommendations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-Key", aiSecretKey)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            consecutiveFailures.set(0);
            return parseResponse(response);
        } catch (Exception e) {
            log.warn("Falla llamando al servicio de IA: {}", e.getMessage());
            int failures = consecutiveFailures.incrementAndGet();
            if (failures >= FAILURE_THRESHOLD) {
                openUntil.set(System.currentTimeMillis() + OPEN_DURATION_MILLIS);
                log.error("Circuit breaker IA abierto durante {} ms tras {} fallos seguidos",
                        OPEN_DURATION_MILLIS, failures);
            }
            return fallback(request);
        }
    }

    private AiRecommendationResponse fallback(AiRecommendationRequest request) {
        List<AiRecommendationResponse.ProductRecommendation> recs = new ArrayList<>();
        if (request.getAvailableProducts() != null) {
            request.getAvailableProducts().stream()
                    .sorted(Comparator
                            .comparing((AiRecommendationRequest.ProductInfo p) -> p.precio() == null
                                    ? BigDecimal.ZERO : p.precio()))
                    .limit(Math.max(1, request.getMaxRecommendations()))
                    .forEach(p -> recs.add(new AiRecommendationResponse.ProductRecommendation(
                            p.id(),
                            p.nombre(),
                            p.precio(),
                            p.categoria(),
                            0.0,
                            "Recomendación por defecto (servicio IA no disponible)"
                    )));
        }
        return AiRecommendationResponse.builder()
                .userId(request.getUserId())
                .recommendations(recs)
                .generatedBy("FALLBACK")
                .build();
    }

    @SuppressWarnings("unchecked")
    private AiRecommendationResponse parseResponse(Map<String, Object> response) {
        List<Map<String, Object>> recs = (List<Map<String, Object>>) response.get("recommendations");

        List<AiRecommendationResponse.ProductRecommendation> recommendations = new ArrayList<>();
        if (recs != null) {
            for (Map<String, Object> rec : recs) {
                recommendations.add(new AiRecommendationResponse.ProductRecommendation(
                        ((Number) rec.get("product_id")).longValue(),
                        (String) rec.get("nombre"),
                        new BigDecimal(rec.get("precio").toString()),
                        (String) rec.get("categoria"),
                        ((Number) rec.get("score")).doubleValue(),
                        (String) rec.get("reason")
                ));
            }
        }

        return AiRecommendationResponse.builder()
                .userId(((Number) response.get("user_id")).longValue())
                .recommendations(recommendations)
                .generatedBy((String) response.get("generated_by"))
                .build();
    }
}
