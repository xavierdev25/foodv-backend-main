package com.foodv.backend.infrastructure.ai;

import com.foodv.backend.domain.model.ai.AiRecommendationRequest;
import com.foodv.backend.domain.model.ai.AiRecommendationResponse;
import com.foodv.backend.domain.port.out.AiRecommendationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AiServiceAdapter implements AiRecommendationPort {

    private final RestClient restClient;
    private final String aiServiceUrl;

    public AiServiceAdapter(RestClient restClient,
                            @Value("${ai.service.url:http://localhost:8001}") String aiServiceUrl) {
        this.restClient = restClient;
        this.aiServiceUrl = aiServiceUrl;
    }

    @Override
    public AiRecommendationResponse getRecommendations(AiRecommendationRequest request) {
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
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            return parseResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("Error llamando al servicio de IA: " + e.getMessage());
        }
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
