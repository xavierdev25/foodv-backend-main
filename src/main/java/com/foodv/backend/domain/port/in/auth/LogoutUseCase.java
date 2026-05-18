package com.foodv.backend.domain.port.in.auth;

public interface LogoutUseCase {

    void execute(LogoutCommand command);

    record LogoutCommand(
            String accessToken,
            String refreshToken
    ) {}
}
