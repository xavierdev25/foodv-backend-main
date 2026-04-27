package com.foodv.backend.infrastructure.web.dto.ai;

public record FeedbackResponse(
        Long id,
        Long userId,
        Long productId,
        Boolean liked,
        String message
) {}