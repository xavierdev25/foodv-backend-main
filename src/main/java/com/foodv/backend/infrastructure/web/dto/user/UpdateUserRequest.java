package com.foodv.backend.infrastructure.web.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank String nombres,
        @NotBlank String apellidos,
        String telefono
) {}
