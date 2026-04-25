package com.foodv.backend.application.aula;

import com.foodv.backend.domain.model.aula.Aula;
import com.foodv.backend.domain.port.in.aula.FindAulaUseCase;
import com.foodv.backend.domain.port.out.AulaRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindAulaHandler implements FindAulaUseCase {

    private final AulaRepositoryPort aulaRepositoryPort;

    @Override
    public Aula findById(Long id) {
        return aulaRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aula no encontrada"));
    }

    @Override
    public List<Aula> findAll() {
        return aulaRepositoryPort.findAll();
    }

    @Override
    public List<Aula> findAllActivas() {
        return aulaRepositoryPort.findAllActivas();
    }
}
