package com.wilson.inkwell.authorization.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.wilson.inkwell.authorization.dto.HttpBodyErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<HttpBodyErrorResponse> handleAuthenticationException(AuthenticationException e) {

        int httpStatus = HttpStatus.UNAUTHORIZED.value();
        HttpBodyErrorResponse body = new HttpBodyErrorResponse(
                httpStatus, "Invalid email or password", "Authentication Error", Instant.now());

        return ResponseEntity.status(httpStatus).body(body);

    }

    @ExceptionHandler(CredentialAlreadyCreatedException.class)
    public ResponseEntity<HttpBodyErrorResponse> handleCredentialAlreadyCreatedException(
            CredentialAlreadyCreatedException e) {

        int httpStatus = HttpStatus.CONFLICT.value();
        HttpBodyErrorResponse body = new HttpBodyErrorResponse(
                httpStatus, "Email already registered", "Registration Error", Instant.now());

        return ResponseEntity.status(httpStatus).body(body);

    }

    // WARN This handler treats only InternalServerErrorExceptions. It works fine for
    // these explicitly thrown exceptions, but spring security's default response
    // for uncaught exceptions is 409 if I remember correctly. I should consider
    // looking into overriding it to a 500 INTERNAL_SERVER_ERROR maybe?
    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<HttpBodyErrorResponse> handleInternalServerErrorException(InternalServerErrorException e) {

        int httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
        HttpBodyErrorResponse body = new HttpBodyErrorResponse(
                httpStatus, "An internal error happened.", "Server Error", Instant.now());

        return ResponseEntity.status(httpStatus).body(body);

    }

}
