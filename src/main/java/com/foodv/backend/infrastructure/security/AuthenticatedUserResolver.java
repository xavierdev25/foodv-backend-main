package com.foodv.backend.infrastructure.security;

import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.domain.model.user.UserRole;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserResolver {

    public User currentUser() {
        return currentUserSummary();
    }

    public User currentUserSummary() {
        AuthenticatedUserPrincipal principal = currentPrincipal();
        return User.builder()
                .id(principal.userId())
                .nombres(principal.nombres())
                .email(principal.email())
                .role(principal.role())
                .activo(true)
                .build();
    }

    public Long currentUserId() {
        return currentPrincipal().userId();
    }

    public String currentEmail() {
        return currentPrincipal().email();
    }

    public UserRole currentRole() {
        return currentPrincipal().role();
    }

    private AuthenticatedUserPrincipal currentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("No autenticado");
        }
        if (auth.getPrincipal() instanceof AuthenticatedUserPrincipal principal) {
            return principal;
        }
        throw new AccessDeniedException("No autenticado");
    }
}
