package com.foodv.backend.infrastructure.web.dto.auth;

import com.foodv.backend.domain.model.user.UserRole;
import jakarta.validation.constraints.*;
import java.util.List;

public record RegisterRequest(
        @NotBlank String nombres,
        @NotBlank String apellidos,
        @NotBlank @Email String email,
        @NotBlank
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
                message = "La contraseña debe tener al menos una mayúscula, una minúscula y un número"
        )
        String password,
        String telefono,
        @NotNull UserRole role,

        // Preferencias gastronómicas
        List<String> preferences,        // ["pollo", "arroz", "menú ejecutivo"]
        List<String> restrictions,       // ["VEGETARIANO", "SIN_GLUTEN"]
        String budgetRange,              // "BAJO" | "MEDIO" | "ALTO"
        List<String> cuisineTypes        // ["criolla", "italiana", "china"]
) {
        public RegisterRequest {
                if (preferences == null) preferences = List.of();
                if (restrictions == null) restrictions = List.of();
                if (budgetRange == null || budgetRange.isBlank()) budgetRange = "MEDIO";
                if (cuisineTypes == null) cuisineTypes = List.of();
        }
}