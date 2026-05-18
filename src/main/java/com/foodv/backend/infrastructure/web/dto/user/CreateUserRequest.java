package com.foodv.backend.infrastructure.web.dto.user;

import com.foodv.backend.domain.model.user.BudgetRange;
import com.foodv.backend.domain.model.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateUserRequest(
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
        @Size(max = 120, message = "Email demasiado largo")
        String email,

        @NotBlank(message = "Password obligatorio")
        @Size(min = 8, max = 128, message = "Password entre 8 y 128 caracteres")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
                message = "Password debe contener al menos una letra y un número"
        )
        String password,

        @Pattern(regexp = "^[+]?[0-9\\s-]{6,20}$", message = "Teléfono inválido")
        String telefono,

        @NotNull(message = "Rol obligatorio")
        UserRole role,

        List<String> preferences,
        List<String> restrictions,
        BudgetRange budgetRange,
        List<String> cuisineTypes
) {
        public CreateUserRequest {
                if (preferences == null) preferences = List.of();
                if (restrictions == null) restrictions = List.of();
                if (budgetRange == null) budgetRange = BudgetRange.MEDIO;
                if (cuisineTypes == null) cuisineTypes = List.of();
        }
}
