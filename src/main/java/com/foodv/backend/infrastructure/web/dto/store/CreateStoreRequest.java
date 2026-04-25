package com.foodv.backend.infrastructure.web.dto.store;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateStoreRequest(
        @NotBlank String nombre,
        String descripcion,
        String telefono,
        @NotNull Long ownerId
) {}
