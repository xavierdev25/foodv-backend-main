package com.foodv.backend.domain.port.in.ai;

import com.foodv.backend.domain.model.ai.AiRecommendationResponse;

import java.util.List;

public interface GetRecommendationsUseCase {

    record GetRecommendationsCommand(Long userId, List<String> restrictions, List<String> preferences, int maxRecommendations) {}

    AiRecommendationResponse execute(GetRecommendationsCommand command);
}
