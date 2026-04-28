package com.foodv.backend.domain.port.out;

public interface SecureRandomPort {

    String generateConfirmationCode(int length);

    String generateRandomString(int length);
}
