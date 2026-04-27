package com.foodv.backend.infrastructure.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Order(1)
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> generalBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> aiBuckets = new ConcurrentHashMap<>();

    private Bucket createGeneralBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(100)
                        .refillGreedy(100, Duration.ofMinutes(1))
                        .build())
                .build();
    }

    private Bucket createAiBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(5)
                        .refillGreedy(5, Duration.ofMinutes(1))
                        .build())
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String clientKey = getClientKey(request);
        boolean isAiEndpoint = request.getRequestURI().contains("/ai/recommendations");

        Bucket bucket = isAiEndpoint
                ? aiBuckets.computeIfAbsent(clientKey, k -> createAiBucket())
                : generalBuckets.computeIfAbsent(clientKey, k -> createGeneralBucket());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            String message = isAiEndpoint
                    ? "Has excedido el límite de 5 recomendaciones por minuto."
                    : "Has excedido el límite de 100 peticiones por minuto.";
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    "{\"status\":429,\"error\":\"Demasiadas peticiones\",\"message\":\"" + message + "\"}"
            );
        }
    }

    private String getClientKey(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return "user:" + authHeader.substring(7, Math.min(authHeader.length(), 20));
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
        return "ip:" + ip;
    }
}