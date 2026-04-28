package com.foodv.backend.domain.port.in.auth;

public interface LogoutUseCase {

    void execute(LogoutCommand command);

    record LogoutCommand(
            Long userId,
            String accessToken,
            String refreshToken
    ) {}
}
