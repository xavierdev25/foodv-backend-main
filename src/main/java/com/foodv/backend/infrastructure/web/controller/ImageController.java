package com.foodv.backend.infrastructure.web.controller;

import com.foodv.backend.domain.port.out.ImageStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageStoragePort imageStoragePort;

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

    @DeleteMapping
    public ResponseEntity<Void> deleteImage(@RequestParam String publicId) {
        imageStoragePort.deleteImage(publicId);
        return ResponseEntity.noContent().build();
    }
}
