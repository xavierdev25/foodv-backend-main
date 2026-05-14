package com.foodv.backend.infrastructure.persistence.adapter;

import com.foodv.backend.domain.model.favorite.FavoriteProduct;
import com.foodv.backend.infrastructure.persistence.entity.FavoriteProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FavoriteProductEntityMapper {

    FavoriteProductEntity toEntity(FavoriteProduct favorite);

    FavoriteProduct toDomain(FavoriteProductEntity entity);
}
