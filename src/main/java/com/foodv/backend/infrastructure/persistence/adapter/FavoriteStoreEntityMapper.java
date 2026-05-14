package com.foodv.backend.infrastructure.persistence.adapter;

import com.foodv.backend.domain.model.favorite.FavoriteStore;
import com.foodv.backend.infrastructure.persistence.entity.FavoriteStoreEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FavoriteStoreEntityMapper {

    FavoriteStoreEntity toEntity(FavoriteStore favorite);

    FavoriteStore toDomain(FavoriteStoreEntity entity);
}
