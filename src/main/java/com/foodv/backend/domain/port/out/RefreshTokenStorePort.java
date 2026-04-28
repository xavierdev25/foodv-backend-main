package com.foodv.backend.domain.port.out;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenStorePort {

    record RefreshTokenInfo(
            Long id,
            String token,
            Long userId,
            LocalDateTime expiresAt,
            boolean revoked
    ) {}

    void save(String token, Long userId, LocalDateTime expiresAt);

    Optional<RefreshTokenInfo> findByToken(String token);

    void revokeByToken(String token);

    void revokeAllByUserId(Long userId);
}
