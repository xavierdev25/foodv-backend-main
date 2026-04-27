package com.foodv.backend.domain.port.in.user;

import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.domain.model.user.UserRole;

import java.util.List;

public interface CreateUserUseCase {

    User execute(CreateUserCommand command);

    record CreateUserCommand(
            String nombres,
            String apellidos,
            String email,
            String password,
            String telefono,
            UserRole role,
            List<String> preferences,
            List<String> restrictions,
            String budgetRange,
            List<String> cuisineTypes
    ) {}

}
