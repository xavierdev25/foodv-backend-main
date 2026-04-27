package com.foodv.backend.infrastructure.web.mapper;

import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.domain.port.in.user.CreateUserUseCase;
import com.foodv.backend.domain.port.in.user.UpdateUserUseCase;
import com.foodv.backend.infrastructure.web.dto.user.CreateUserRequest;
import com.foodv.backend.infrastructure.web.dto.user.UpdateUserRequest;
import com.foodv.backend.infrastructure.web.dto.user.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserWebMapper {

    UserResponse toResponse(User user);

    @Mapping(target = "preferences", ignore = true)
    @Mapping(target = "restrictions", ignore = true)
    @Mapping(target = "budgetRange", ignore = true)
    @Mapping(target = "cuisineTypes", ignore = true)
    CreateUserUseCase.CreateUserCommand toCommand(CreateUserRequest request);

    UpdateUserUseCase.UpdateUserCommand toCommand(UpdateUserRequest request);
}
