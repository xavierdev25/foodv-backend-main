package com.foodv.backend.infrastructure.security;

import com.foodv.backend.domain.model.user.UserRole;
import org.springframework.security.core.AuthenticatedPrincipal;

public record AuthenticatedUserPrincipal(
        Long userId,
        String email,
        UserRole role,
        String nombres
) implements AuthenticatedPrincipal {

    @Override
    public String getName() {
        return email;
    }
}
