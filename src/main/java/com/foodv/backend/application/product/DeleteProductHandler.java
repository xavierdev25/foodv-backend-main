package com.foodv.backend.application.product;

import com.foodv.backend.domain.port.in.product.DeleteProductUseCase;
import com.foodv.backend.domain.port.out.ProductRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteProductHandler implements DeleteProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    @Override
    public void execute(Long id) {
        productRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        productRepositoryPort.deleteById(id);
    }
}
