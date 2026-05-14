package com.foodv.backend.domain.model.rating;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Rating {

    @EqualsAndHashCode.Include
    private Long id;

    private Long orderId;
    private Long userId;
    private Long storeId;
    private Integer rating;
    private String comentario;
    private LocalDateTime creadoEn;
}
