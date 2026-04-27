package com.foodv.backend.infrastructure.web.controller;

import com.foodv.backend.domain.port.out.ImageStoragePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Tag(name = "Imágenes")
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageStoragePort imageStoragePort;

    @Operation(summary = "Subir imagen de producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imagen subida"),
        @ApiResponse(responseCode = "400", description = "Archivo inválido"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping("/products/{productId}")
    public ResponseEntity<Map<String, String>> uploadProductImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }
        String imageUrl = imageStoragePort.uploadImage(file.getBytes(), file.getOriginalFilename(), "foodv/products");
        return ResponseEntity.ok(Map.of("url", imageUrl));
    }

    @Operation(summary = "Subir imagen de tienda")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imagen subida"),
        @ApiResponse(responseCode = "400", description = "Archivo inválido"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping("/stores/{storeId}")
    public ResponseEntity<Map<String, String>> uploadStoreImage(
            @PathVariable Long storeId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }
        String imageUrl = imageStoragePort.uploadImage(file.getBytes(), file.getOriginalFilename(), "foodv/stores");
        return ResponseEntity.ok(Map.of("url", imageUrl));
    }

    @Operation(summary = "Eliminar imagen")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Imagen eliminada"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteImage(@RequestParam String publicId) {
        imageStoragePort.deleteImage(publicId);
        return ResponseEntity.noContent().build();
    }
}
