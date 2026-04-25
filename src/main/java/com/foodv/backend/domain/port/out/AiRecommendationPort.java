package com.foodv.backend.domain.port.out;

import com.foodv.backend.domain.model.ai.AiRecommendationRequest;
import com.foodv.backend.domain.model.ai.AiRecommendationResponse;

public interface AiRecommendationPort {

    AiRecommendationResponse getRecommendations(AiRecommendationRequest request);
}
