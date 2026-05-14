package com.foodv.backend.infrastructure.web.dto.rating;

public record StoreRatingSummaryResponse(
        Long storeId,
        Double promedio,
        Long total
) {}
