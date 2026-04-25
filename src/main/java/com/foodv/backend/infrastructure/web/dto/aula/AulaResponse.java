package com.foodv.backend.infrastructure.web.dto.aula;

public record AulaResponse(
        Long id,
        String codigo,
        String nombre,
        String piso,
        String pabellon,
        boolean activo
) {}
