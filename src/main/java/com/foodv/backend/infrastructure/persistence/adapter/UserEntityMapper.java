package com.foodv.backend.infrastructure.persistence.adapter;

import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {

    UserEntity toEntity(User user);

    User toDomain(UserEntity entity);
}
