package com.foodv.backend.application.product;

import com.foodv.backend.domain.model.product.Product;
import com.foodv.backend.domain.port.in.product.UpdateProductUseCase;
import com.foodv.backend.domain.port.out.ProductRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpdateProductHandler implements UpdateProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public Product execute(Long id, UpdateProductCommand command) {
        Product existing = productRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        if (command.precio() != null && command.precio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        if (command.stock() != null && command.stock() < 0) {
            throw new IllegalArgumentException("Stock inválido");
        }

        Product product = Product.builder()
                .id(existing.getId())
                .nombre(command.nombre() != null ? command.nombre().trim() : existing.getNombre())
                .descripcion(command.descripcion() != null ? command.descripcion().trim() : existing.getDescripcion())
                .precio(command.precio() != null ? command.precio() : existing.getPrecio())
                .stock(command.stock() != null ? command.stock() : existing.getStock())
                .categoria(command.categoria() != null ? command.categoria() : existing.getCategoria())
                .disponible(command.disponible() != null ? command.disponible() : existing.isDisponible())
                .imagenUrl(existing.getImagenUrl())
                .storeId(existing.getStoreId())
                .activo(existing.isActivo())
                .creadoEn(existing.getCreadoEn())
                .actualizadoEn(LocalDateTime.now())
                .build();

        return productRepositoryPort.save(product);
    }
}
