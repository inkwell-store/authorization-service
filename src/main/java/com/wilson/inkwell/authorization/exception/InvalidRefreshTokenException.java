package com.wilson.inkwell.authorization.exception;

/**
 * Exception thrown when an operation involving a refresh token fails
 * because the token is not valid (expired, revoked etc)
 */
public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException(String message) {
        super(message);
    }
    
}
