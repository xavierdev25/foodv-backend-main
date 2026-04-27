package com.foodv.backend.infrastructure.web.controller;

import com.foodv.backend.domain.model.product.Product;
import com.foodv.backend.domain.model.product.ProductCategory;
import com.foodv.backend.domain.port.in.product.CreateProductUseCase;
import com.foodv.backend.domain.port.in.product.DeleteProductUseCase;
import com.foodv.backend.domain.port.in.product.FindProductUseCase;
import com.foodv.backend.domain.port.in.product.UpdateProductUseCase;
import com.foodv.backend.infrastructure.web.dto.product.CreateProductRequest;
import com.foodv.backend.infrastructure.web.dto.product.ProductResponse;
import com.foodv.backend.infrastructure.web.dto.product.UpdateProductRequest;
import com.foodv.backend.infrastructure.web.mapper.ProductWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Productos")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final FindProductUseCase findProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final ProductWebMapper mapper;

    @Operation(summary = "Crear producto")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        Product product = createProductUseCase.execute(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(product));
    }

    @Operation(summary = "Listar productos paginado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Página de productos"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(findProductUseCase.findAllPaginated(pageable).map(mapper::toResponse));
    }

    @Operation(summary = "Obtener producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        Product product = findProductUseCase.findById(id);
        return ResponseEntity.ok(mapper.toResponse(product));
    }

    @Operation(summary = "Productos por tienda")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Productos de la tienda"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/store/{storeId}")
    public ResponseEntity<Page<ProductResponse>> findByStoreId(
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(findProductUseCase.findByStoreIdPaginated(storeId, pageable).map(mapper::toResponse));
    }

    @Operation(summary = "Productos activos por tienda")
    @GetMapping("/store/{storeId}/activos")
    public ResponseEntity<List<ProductResponse>> findByStoreIdActivos(@PathVariable Long storeId) {
        List<ProductResponse> responses = findProductUseCase.findByStoreIdActivos(storeId).stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Productos por categoría")
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProductResponse>> findByCategoria(@PathVariable ProductCategory categoria) {
        List<ProductResponse> responses = findProductUseCase.findByCategoria(categoria).stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Actualizar producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        Product product = updateProductUseCase.execute(id, mapper.toCommand(request));
        return ResponseEntity.ok(mapper.toResponse(product));
    }

    @Operation(summary = "Eliminar producto")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Producto eliminado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteProductUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar productos con filtros")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resultados de búsqueda"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> search(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) ProductCategory categoria,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) Boolean disponible,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "nombre") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(
                findProductUseCase.search(nombre, categoria, storeId, precioMin, precioMax, disponible, pageable)
                        .map(mapper::toResponse)
        );
    }
}
