package com.foodv.backend.domain.port.out;

public interface ImageStoragePort {

    String uploadImage(byte[] imageBytes, String filename, String folder);

    void deleteImage(String publicId);
}
