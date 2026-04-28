package com.foodv.backend.infrastructure.security;

import com.foodv.backend.domain.port.out.LoginAttemptPort;
import org.springframework.stereotype.Service;

/**
 * @deprecated mantener únicamente para compatibilidad temporal con código legacy.
 * Inyectar {@link LoginAttemptPort} en su lugar.
 */
@Deprecated
@Service
public class LoginAttemptService {

    private final LoginAttemptPort loginAttemptPort;

    public LoginAttemptService(LoginAttemptPort loginAttemptPort) {
        this.loginAttemptPort = loginAttemptPort;
    }

    public void recordFailedAttempt(String email) {
        loginAttemptPort.recordFailedAttempt(email);
    }

    public void resetAttempts(String email) {
        loginAttemptPort.resetAttempts(email);
    }

    public boolean isBlocked(String email) {
        return loginAttemptPort.isBlocked(email);
    }
}
