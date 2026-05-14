package com.foodv.backend.infrastructure.web.dto.rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateRatingRequest(
        @NotNull(message = "Rating obligatorio")
        @Min(value = 1, message = "Rating mínimo 1")
        @Max(value = 5, message = "Rating máximo 5")
        Integer rating,

        @Size(max = 500, message = "Comentario máximo 500 caracteres")
        String comentario
) {}
