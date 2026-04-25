package com.foodv.backend.domain.port.in.user;

import com.foodv.backend.domain.model.user.User;

public interface UpdateUserUseCase {

    User execute(Long id, UpdateUserCommand command);

    record UpdateUserCommand(
            String nombres,
            String apellidos,
            String telefono
    ) {}
}
