package com.foodv.backend.infrastructure.security;

import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.domain.port.out.UserRepositoryPort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserResolver {

    private final UserRepositoryPort userRepositoryPort;

    public AuthenticatedUserResolver(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            throw new AccessDeniedException("No autenticado");
        }
        return userRepositoryPort.findByEmail(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
    }

    public Long currentUserId() {
        return currentUser().getId();
    }

    public String currentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new AccessDeniedException("No autenticado");
        }
        return auth.getName();
    }
}
