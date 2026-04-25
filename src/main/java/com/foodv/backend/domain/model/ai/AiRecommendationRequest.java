package com.foodv.backend.domain.model.ai;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class AiRecommendationRequest {

    private Long userId;
    private List<String> restrictions;
    private List<String> preferences;
    private List<ProductInfo> availableProducts;
    private int maxRecommendations;

    public record ProductInfo(Long id, String nombre, BigDecimal precio, String categoria) {}
}
