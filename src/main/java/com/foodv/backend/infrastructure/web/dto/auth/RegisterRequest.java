package com.foodv.backend.infrastructure.web.dto.auth;

import com.foodv.backend.domain.model.user.UserRole;
import jakarta.validation.constraints.*;

import java.util.List;

public record RegisterRequest(
        @NotBlank(message = "Nombres obligatorios")
        @Size(min = 2, max = 60, message = "Nombres entre 2 y 60 caracteres")
        @Pattern(regexp = "^[\\p{L} '-]+$", message = "Nombres con caracteres inválidos")
        String nombres,

        @NotBlank(message = "Apellidos obligatorios")
        @Size(min = 2, max = 60, message = "Apellidos entre 2 y 60 caracteres")
        @Pattern(regexp = "^[\\p{L} '-]+$", message = "Apellidos con caracteres inválidos")
        String apellidos,

        @NotBlank(message = "Email obligatorio")
        @Email(message = "Email inválido")
        @Size(max = 120)
        String email,

        @NotBlank(message = "Password obligatorio")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
                message = "La contraseña debe tener al menos una mayúscula, una minúscula y un número"
        )
        String password,

        @Pattern(regexp = "^[+]?[0-9\\s-]{6,20}$", message = "Teléfono inválido")
        String telefono,

        @NotNull(message = "Rol obligatorio")
        UserRole role,

        List<String> preferences,
        List<String> restrictions,
        String budgetRange,
        List<String> cuisineTypes
) {
        public RegisterRequest {
                if (preferences == null) preferences = List.of();
                if (restrictions == null) restrictions = List.of();
                if (budgetRange == null || budgetRange.isBlank()) budgetRange = "MEDIO";
                if (cuisineTypes == null) cuisineTypes = List.of();
        }
}
