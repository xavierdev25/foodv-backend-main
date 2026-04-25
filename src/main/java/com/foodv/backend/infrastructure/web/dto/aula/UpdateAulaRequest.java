package com.foodv.backend.infrastructure.web.dto.aula;

import jakarta.validation.constraints.NotBlank;

public record UpdateAulaRequest(
        @NotBlank String nombre,
        String piso,
        String pabellon
) {}
