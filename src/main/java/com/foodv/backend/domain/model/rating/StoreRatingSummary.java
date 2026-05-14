package com.foodv.backend.domain.model.rating;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreRatingSummary {

    private Long storeId;
    private Double promedio;
    private Long total;
}
