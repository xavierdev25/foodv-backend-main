package com.foodv.backend.domain.model.aula;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Aula {

    @EqualsAndHashCode.Include
    private Long id;

    private String codigo;
    private String nombre;
    private String piso;
    private String pabellon;
    private boolean activo;
}
