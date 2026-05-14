package com.foodv.backend.infrastructure.persistence.adapter;

import com.foodv.backend.domain.model.rating.Rating;
import com.foodv.backend.infrastructure.persistence.entity.RatingEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RatingEntityMapper {

    RatingEntity toEntity(Rating rating);

    Rating toDomain(RatingEntity entity);
}
