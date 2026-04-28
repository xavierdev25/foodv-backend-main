package com.foodv.backend.infrastructure.security;

import com.foodv.backend.domain.port.out.TokenBlacklistPort;
import com.foodv.backend.domain.port.out.TokenServicePort;
import com.foodv.backend.domain.port.out.UserRepositoryPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Order(2)
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenServicePort tokenServicePort;
    private final TokenBlacklistPort tokenBlacklistPort;
    private final UserRepositoryPort userRepositoryPort;
    private final SecretKey signingKey;

    public JwtAuthenticationFilter(TokenServicePort tokenServicePort,
                                   TokenBlacklistPort tokenBlacklistPort,
                                   UserRepositoryPort userRepositoryPort,
                                   com.foodv.backend.infrastructure.config.JwtConfig jwtConfig) {
        this.tokenServicePort = tokenServicePort;
        this.tokenBlacklistPort = tokenBlacklistPort;
        this.userRepositoryPort = userRepositoryPort;
        this.signingKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (tokenBlacklistPort.isBlacklisted(token) || !tokenServicePort.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims;
        try {
            claims = Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        String email = claims.getSubject();
        String role = claims.get("role", String.class);
        long issuedAtMillis = claims.getIssuedAt() != null ? claims.getIssuedAt().getTime() : 0L;

        // Si la sesión del usuario fue invalidada (cambio de password, delete), rechazar
        Long userId = userRepositoryPort.findByEmail(email).map(u -> u.getId()).orElse(null);
        if (userId != null && tokenBlacklistPort.isUserSessionInvalidated(userId, issuedAtMillis)) {
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        role == null ? List.of() : List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
