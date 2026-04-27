package com.foodv.backend.infrastructure.web.dto.ai;

import jakarta.validation.constraints.NotNull;

public record FeedbackRequest(
        @NotNull Long productId,
        @NotNull Boolean liked
) {}