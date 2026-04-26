package com.foodv.backend.domain.port.in.user;

public interface ChangePasswordUseCase {
    void execute(String email, String currentPassword, String newPassword);
}
