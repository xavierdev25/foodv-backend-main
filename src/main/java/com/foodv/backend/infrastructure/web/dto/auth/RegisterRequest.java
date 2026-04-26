package com.foodv.backend.infrastructure.web.dto.auth;

import com.foodv.backend.domain.model.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

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
        @NotNull UserRole role
) {}
