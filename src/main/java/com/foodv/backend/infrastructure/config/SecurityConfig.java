package com.foodv.backend.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import com.foodv.backend.infrastructure.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Públicos sin autenticación
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info", "/actuator/prometheus").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**").permitAll()
                        .requestMatchers("/payments/webhook").permitAll()

                        // Users — rutas propias (cualquier autenticado)
                        .requestMatchers(HttpMethod.GET, "/users/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/users/me/password").authenticated()

                        // Users — administración (solo ADMIN)
                        .requestMatchers(HttpMethod.GET, "/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")

                        // Aulas
                        .requestMatchers(HttpMethod.GET, "/aulas/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/aulas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/aulas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/aulas/**").hasRole("ADMIN")

                        // Stores
                        .requestMatchers(HttpMethod.GET, "/stores/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/stores/**").hasAnyRole("ADMIN", "COMERCIO")
                        .requestMatchers(HttpMethod.PUT, "/stores/**").hasAnyRole("ADMIN", "COMERCIO")
                        .requestMatchers(HttpMethod.DELETE, "/stores/**").hasRole("ADMIN")

                        // Products
                        .requestMatchers(HttpMethod.GET, "/products/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/products/**").hasAnyRole("ADMIN", "COMERCIO")
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasAnyRole("ADMIN", "COMERCIO")
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasAnyRole("ADMIN", "COMERCIO")

                        // Orders
                        .requestMatchers(HttpMethod.POST, "/orders").hasAnyRole("ESTUDIANTE", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/orders/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/orders/**").hasAnyRole("COMERCIO", "REPARTIDOR", "ADMIN")

                        // Payments
                        .requestMatchers("/payments/**").authenticated()

                        // AI
                        .requestMatchers("/ai/**").authenticated()

                        // Images
                        .requestMatchers(HttpMethod.POST, "/images/**").hasAnyRole("ADMIN", "COMERCIO")
                        .requestMatchers(HttpMethod.DELETE, "/images/**").hasAnyRole("ADMIN", "COMERCIO")

                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
