package com.foodv.backend.domain.factory;

import com.foodv.backend.domain.model.user.BudgetRange;
import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.domain.model.user.UserRole;

import java.time.LocalDateTime;
import java.util.List;

public final class UserFactory {

    private UserFactory() {
    }

    public static User create(String nombres,
                              String apellidos,
                              String email,
                              String encodedPassword,
                              String telefono,
                              UserRole role,
                              List<String> preferences,
                              List<String> restrictions,
                              BudgetRange budgetRange,
                              List<String> cuisineTypes) {
        return User.builder()
                .nombres(trimToNull(nombres))
                .apellidos(trimToNull(apellidos))
                .email(normalizeEmail(email))
                .password(encodedPassword)
                .telefono(trimToNull(telefono))
                .role(role)
                .activo(true)
                .creadoEn(LocalDateTime.now())
                .preferences(preferences != null ? preferences : List.of())
                .restrictions(restrictions != null ? restrictions : List.of())
                .budgetRange(budgetRange != null ? budgetRange : BudgetRange.MEDIO)
                .cuisineTypes(cuisineTypes != null ? cuisineTypes : List.of())
                .build();
    }

    public static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private static String trimToNull(String value) {
        return value == null ? null : value.trim();
    }
}
