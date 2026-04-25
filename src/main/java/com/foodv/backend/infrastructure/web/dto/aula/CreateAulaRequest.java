package com.foodv.backend.infrastructure.web.dto.aula;

import jakarta.validation.constraints.NotBlank;

public record CreateAulaRequest(
        @NotBlank String codigo,
        @NotBlank String nombre,
        String piso,
        String pabellon
) {}
