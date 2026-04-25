package com.foodv.backend.infrastructure.web.dto.user;

import com.foodv.backend.domain.model.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank String nombres,
        @NotBlank String apellidos,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8) String password,
        String telefono,
        @NotNull UserRole role
) {}
