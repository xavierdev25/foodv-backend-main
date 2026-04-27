package com.foodv.backend.infrastructure.scheduler;

import com.foodv.backend.infrastructure.persistence.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TokenCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(TokenCleanupScheduler.class);
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenCleanupScheduler(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // Corre todos los días a las 3:00 AM
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        refreshTokenRepository.deleteAllExpired(now);
        refreshTokenRepository.deleteRevokedBefore(now.minusDays(1));
        log.info("Limpieza de refresh tokens expirados completada");
    }
}