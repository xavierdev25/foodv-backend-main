package com.foodv.backend.infrastructure.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.foodv.backend.domain.port.out.ImageStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CloudinaryAdapter implements ImageStoragePort {

    private final Cloudinary cloudinary;

    @Override
    public String uploadImage(byte[] imageBytes, String filename, String folder) {
        try {
            Map options = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "image"
            );
            Map result = cloudinary.uploader().upload(imageBytes, options);
            return (String) result.get("secure_url");
        } catch (Exception e) {
            throw new RuntimeException("Error subiendo imagen: " + e.getMessage());
        }
    }

    @Override
    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            log.warn("Error eliminando imagen con publicId {}: {}", publicId, e.getMessage());
        }
    }
}
