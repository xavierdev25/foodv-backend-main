package com.foodv.backend.infrastructure.web.dto.store;

import java.time.LocalDateTime;

public record StoreResponse(
        Long id,
        String nombre,
        String descripcion,
        String imagenUrl,
        String telefono,
        Long ownerId,
        boolean activo,
        LocalDateTime creadoEn,
        String horarioApertura,
        String horarioCierre
) {}
