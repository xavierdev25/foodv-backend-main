package com.foodv.backend.domain.exception;

/**
 * Indicates that the authenticated user is not authorized to perform an operation.
 */
public class AuthorizationException extends RuntimeException {

    public AuthorizationException(String message) {
        super(message);
    }
}
