package com.foodv.backend.infrastructure.web.dto.store;

import jakarta.validation.constraints.NotBlank;

public record UpdateStoreRequest(
        @NotBlank String nombre,
        String descripcion,
        String telefono
) {}
