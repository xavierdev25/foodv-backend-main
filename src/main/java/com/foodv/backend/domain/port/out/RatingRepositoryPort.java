package com.foodv.backend.domain.port.out;

import com.foodv.backend.domain.model.rating.Rating;
import com.foodv.backend.domain.model.rating.StoreRatingSummary;

import java.util.List;
import java.util.Optional;

public interface RatingRepositoryPort {

    Rating save(Rating rating);

    Optional<Rating> findByOrderId(Long orderId);

    List<Rating> findByStoreId(Long storeId);

    boolean existsByOrderId(Long orderId);

    StoreRatingSummary findStoreRatingSummary(Long storeId);
}
