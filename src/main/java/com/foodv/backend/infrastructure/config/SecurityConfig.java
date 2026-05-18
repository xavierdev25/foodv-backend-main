package com.foodv.backend.infrastructure.config;

import com.foodv.backend.infrastructure.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final Environment environment;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, Environment environment) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.environment = environment;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())  // API stateless con tokens Bearer; CSRF no aplica
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                        configureActuatorAccess(auth);
                        configureDocumentationAccess(auth);
                        auth
                        // Endpoints estrictamente públicos
                        .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/register", "/auth/refresh",
                                "/api/auth/login", "/api/auth/register", "/api/auth/refresh").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/error").permitAll()// handshake; auth se valida por ChannelInterceptor

                        // Logout requiere autenticación (para revocar tokens propios)
                        .requestMatchers(HttpMethod.POST, "/auth/logout", "/api/auth/logout").authenticated()

                        // Mi perfil
                        .requestMatchers(HttpMethod.GET, "/users/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/users/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/users/me/password").authenticated()

                        // Endpoints de pagos del usuario
                        .requestMatchers(HttpMethod.GET, "/payments/me").authenticated()

                        // Aulas
                        .requestMatchers(HttpMethod.GET, "/aulas/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/aulas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/aulas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/aulas/**").hasRole("ADMIN")

                        // Users — administración
                        .requestMatchers("/users/deleted", "/users/*/restore").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/{id}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/users/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/users/{id}").authenticated()

                        // Stores
                        .requestMatchers(HttpMethod.GET, "/stores/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/stores/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/stores/**").hasAnyRole("ADMIN", "COMERCIO")
                        .requestMatchers(HttpMethod.PUT, "/stores/**").hasAnyRole("ADMIN", "COMERCIO")
                        .requestMatchers(HttpMethod.DELETE, "/stores/**").hasAnyRole("ADMIN", "COMERCIO")

                        // Products
                        .requestMatchers(HttpMethod.GET, "/products/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/products/**").hasAnyRole("ADMIN", "COMERCIO")
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasAnyRole("ADMIN", "COMERCIO")
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasAnyRole("ADMIN", "COMERCIO")

                        // Favorites
                        .requestMatchers("/favorites/**").authenticated()

                        // Ratings
                        .requestMatchers(HttpMethod.GET, "/ratings/stores/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/ratings/orders/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/ratings/orders/**").authenticated()

                        // Orders
                        .requestMatchers(HttpMethod.POST, "/orders").hasAnyRole("ESTUDIANTE", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/orders/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/orders/**").authenticated()

                        // Payments
                        .requestMatchers(HttpMethod.POST, "/payments/webhook").permitAll()
                        .requestMatchers("/payments/**").authenticated()
                        // AI
                        .requestMatchers("/ai/**").authenticated()

                        // Images
                        .requestMatchers(HttpMethod.POST, "/images/**").hasAnyRole("ADMIN", "COMERCIO")
                        .requestMatchers(HttpMethod.DELETE, "/images/**").hasAnyRole("ADMIN", "COMERCIO")

                        .anyRequest().authenticated()
                        ;
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private void configureActuatorAccess(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        if (environment.acceptsProfiles(Profiles.of("prod"))) {
            auth.requestMatchers("/actuator/**", "/api/actuator/**").hasRole("ADMIN");
            return;
        }
        auth.requestMatchers("/actuator/health", "/actuator/info", "/actuator/prometheus").permitAll();
    }

    private void configureDocumentationAccess(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        if (environment.acceptsProfiles(Profiles.of("prod"))) {
            auth.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/v3/api-docs/**")
                    .hasRole("ADMIN");
            return;
        }
        auth.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/v3/api-docs/**").permitAll();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
