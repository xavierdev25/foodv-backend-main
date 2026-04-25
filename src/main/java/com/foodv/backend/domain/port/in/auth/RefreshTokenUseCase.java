package com.foodv.backend.domain.port.in.auth;

public interface RefreshTokenUseCase {

    LoginUseCase.LoginResult execute(String refreshToken);
}
