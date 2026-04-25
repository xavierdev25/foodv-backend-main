package com.foodv.backend.domain.port.in.auth;

public interface LoginUseCase {

    LoginResult execute(LoginCommand command);

    record LoginCommand(
            String email,
            String password
    ) {}

    record LoginResult(
            String accessToken,
            String refreshToken,
            String tokenType,
            long expiresIn
    ) {}
}
