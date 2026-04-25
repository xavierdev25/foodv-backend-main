package com.foodv.backend.application.aula;

import com.foodv.backend.domain.port.in.aula.DeleteAulaUseCase;
import com.foodv.backend.domain.port.out.AulaRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteAulaHandler implements DeleteAulaUseCase {

    private final AulaRepositoryPort aulaRepositoryPort;

    @Override
    public void execute(Long id) {
        aulaRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aula no encontrada"));

        aulaRepositoryPort.deleteById(id);
    }
}
