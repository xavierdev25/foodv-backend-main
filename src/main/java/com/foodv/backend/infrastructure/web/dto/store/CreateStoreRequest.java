package com.foodv.backend.infrastructure.web.dto.store;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateStoreRequest(
        @NotBlank(message = "Nombre obligatorio")
        @Size(min = 2, max = 100, message = "Nombre debe tener entre 2 y 100 caracteres")
        String nombre,

        @Size(max = 500, message = "Descripción no puede exceder 500 caracteres")
        String descripcion,

        @Pattern(regexp = "^[+]?[0-9\\s-]{6,20}$", message = "Teléfono inválido")
        String telefono
) {}
