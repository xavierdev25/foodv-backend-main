package com.foodv.backend.application.product;

import com.foodv.backend.domain.model.product.Product;
import com.foodv.backend.domain.port.in.product.UpdateProductUseCase;
import com.foodv.backend.domain.port.out.ProductRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateProductHandler implements UpdateProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    @Override
    public Product execute(Long id, UpdateProductCommand command) {
        Product existing = productRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        Product product = Product.builder()
                .id(existing.getId())
                .nombre(command.nombre())
                .descripcion(command.descripcion())
                .precio(command.precio())
                .stock(command.stock())
                .categoria(command.categoria())
                .disponible(command.disponible())
                .imagenUrl(existing.getImagenUrl())
                .storeId(existing.getStoreId())
                .activo(existing.isActivo())
                .creadoEn(existing.getCreadoEn())
                .build();

        return productRepositoryPort.save(product);
    }
}
