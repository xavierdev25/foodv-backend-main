package com.foodv.backend.infrastructure.web.dto.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(min = 2, max = 60) String nombres,
        @Size(min = 2, max = 60) String apellidos,
        @Pattern(regexp = "^[+]?[0-9\\s-]{6,20}$", message = "Teléfono inválido") String telefono
) {}
