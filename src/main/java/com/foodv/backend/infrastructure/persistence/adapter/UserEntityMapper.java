package com.foodv.backend.infrastructure.persistence.adapter;

import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {

    @Mapping(target = "deletedAt", ignore = true)
    UserEntity toEntity(User user);

    User toDomain(UserEntity entity);
}