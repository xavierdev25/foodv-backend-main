package com.foodv.backend.domain.port.in.user;

import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.domain.model.user.UserRole;

public interface CreateUserUseCase {

    User execute(CreateUserCommand command);

    record CreateUserCommand(
            String nombres,
            String apellidos,
            String email,
            String password,
            String telefono,
            UserRole role
    ) {}
}
