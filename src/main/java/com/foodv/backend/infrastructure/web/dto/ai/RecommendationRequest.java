package com.foodv.backend.infrastructure.web.dto.ai;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RecommendationRequest(
        @NotNull Long userId,
        List<String> restrictions,
        List<String> preferences,
        @Min(1) @Max(20) int maxRecommendations
) {
    public RecommendationRequest {
        if (restrictions == null) restrictions = List.of();
        if (preferences == null) preferences = List.of();
        if (maxRecommendations == 0) maxRecommendations = 5;
    }
}
