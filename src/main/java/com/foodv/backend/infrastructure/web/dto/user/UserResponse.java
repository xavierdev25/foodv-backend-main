package com.foodv.backend.infrastructure.web.dto.user;

import com.foodv.backend.domain.model.user.BudgetRange;
import com.foodv.backend.domain.model.user.UserRole;
import java.time.LocalDateTime;
import java.util.List;

public record UserResponse(
        Long id,
        String nombres,
        String apellidos,
        String email,
        String telefono,
        UserRole role,
        Boolean esRepartidor,
        Long campusId,
        boolean activo,
        LocalDateTime creadoEn,
        List<String> preferences,
        List<String> restrictions,
        BudgetRange budgetRange,
        List<String> cuisineTypes
) {}
