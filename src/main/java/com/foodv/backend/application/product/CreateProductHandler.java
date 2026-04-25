package com.foodv.backend.application.product;

import com.foodv.backend.domain.model.product.Product;
import com.foodv.backend.domain.model.store.Store;
import com.foodv.backend.domain.port.in.product.CreateProductUseCase;
import com.foodv.backend.domain.port.out.ProductRepositoryPort;
import com.foodv.backend.domain.port.out.StoreRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateProductHandler implements CreateProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final StoreRepositoryPort storeRepositoryPort;

    @Override
    public Product execute(CreateProductCommand command) {
        Store store = storeRepositoryPort.findById(command.storeId())
                .orElseThrow(() -> new EntityNotFoundException("Tienda no encontrada"));

        if (!store.isActivo()) {
            throw new IllegalArgumentException("La tienda no está activa");
        }

        Product product = Product.builder()
                .nombre(command.nombre())
                .descripcion(command.descripcion())
                .precio(command.precio())
                .stock(command.stock())
                .categoria(command.categoria())
                .storeId(command.storeId())
                .activo(true)
                .disponible(true)
                .creadoEn(LocalDateTime.now())
                .build();

        return productRepositoryPort.save(product);
    }
}
