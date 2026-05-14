package com.foodv.backend.infrastructure.web.dto.favorite;

import java.time.LocalDateTime;

public record FavoriteProductResponse(
        Long id,
        Long userId,
        Long productId,
        LocalDateTime creadoEn
) {}
