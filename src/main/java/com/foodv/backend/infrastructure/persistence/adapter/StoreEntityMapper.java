package com.foodv.backend.infrastructure.persistence.adapter;

import com.foodv.backend.domain.model.store.Store;
import com.foodv.backend.infrastructure.persistence.entity.StoreEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StoreEntityMapper {

    @Mapping(target = "deletedAt", ignore = true)
    StoreEntity toEntity(Store store);

    Store toDomain(StoreEntity entity);
}