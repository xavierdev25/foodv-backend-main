package com.foodv.backend.infrastructure.web.dto.rating;

import java.time.LocalDateTime;

public record RatingResponse(
        Long id,
        Long orderId,
        Long userId,
        Long storeId,
        Integer rating,
        String comentario,
        LocalDateTime creadoEn
) {}
