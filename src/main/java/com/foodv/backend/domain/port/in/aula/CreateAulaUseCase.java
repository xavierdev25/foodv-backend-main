package com.foodv.backend.domain.port.in.aula;

import com.foodv.backend.domain.model.aula.Aula;

public interface CreateAulaUseCase {

    record CreateAulaCommand(String codigo, String nombre, String piso, String pabellon) {}

    Aula execute(CreateAulaCommand command);
}
