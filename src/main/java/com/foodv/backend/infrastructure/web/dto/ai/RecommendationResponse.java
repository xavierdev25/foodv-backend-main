package com.foodv.backend.infrastructure.web.dto.ai;

import java.math.BigDecimal;
import java.util.List;

public record RecommendationResponse(
        Long userId,
        List<ProductRecommendationDto> recommendations,
        String generatedBy
) {
    public record ProductRecommendationDto(Long productId, String nombre, BigDecimal precio, String categoria, double score, String reason) {}
}
