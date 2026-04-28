package com.foodv.backend.domain.port.out;

public interface TokenBlacklistPort {

    void blacklist(String token, long expirationMillis);

    boolean isBlacklisted(String token);

    /**
     * Marca todas las sesiones de un usuario como invalidadas a partir de ahora.
     * Útil tras cambio de contraseña.
     */
    void invalidateAllSessionsBefore(Long userId, long timestampMillis);

    /**
     * @return true si el token fue emitido antes del cutoff de invalidación del usuario.
     */
    boolean isUserSessionInvalidated(Long userId, long tokenIssuedAtMillis);
}
