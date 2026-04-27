package com.foodv.backend.infrastructure.persistence.adapter;

import com.foodv.backend.domain.model.product.Product;
import com.foodv.backend.infrastructure.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductEntityMapper {

    @Mapping(target = "deletedAt", ignore = true)
    ProductEntity toEntity(Product product);

    Product toDomain(ProductEntity entity);
}