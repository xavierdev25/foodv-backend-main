package com.foodv.backend.infrastructure.web.dto.favorite;

import java.time.LocalDateTime;

public record FavoriteStoreResponse(
        Long id,
        Long userId,
        Long storeId,
        LocalDateTime creadoEn
) {}
