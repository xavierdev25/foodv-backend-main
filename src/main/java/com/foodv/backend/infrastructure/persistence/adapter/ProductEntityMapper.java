package com.foodv.backend.infrastructure.persistence.adapter;

import com.foodv.backend.domain.model.product.Product;
import com.foodv.backend.infrastructure.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductEntityMapper {

    ProductEntity toEntity(Product product);

    Product toDomain(ProductEntity entity);
}
