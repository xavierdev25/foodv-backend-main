package com.foodv.backend.domain.model.user;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @EqualsAndHashCode.Include
    private Long id;

    private String nombres;
    private String apellidos;
    private String email;
    private String password;
    private String telefono;
    private UserRole role;
    private boolean activo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private List<String> preferences;
    private List<String> restrictions;
    private String budgetRange;
    private List<String> cuisineTypes;
}
