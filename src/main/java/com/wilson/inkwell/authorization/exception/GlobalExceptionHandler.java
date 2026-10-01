package com.wilson.inkwell.authorization.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.wilson.inkwell.authorization.dto.HttpErrorResponseBody;
import com.wilson.inkwell.authorization.dto.HttpMultipleErrorResponseBody;
import com.wilson.inkwell.authorization.enums.HttpErrorTypeEnum;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<HttpErrorResponseBody> handleAuthenticationException(AuthenticationException e) {

        int httpStatus = HttpStatus.UNAUTHORIZED.value();
        HttpErrorResponseBody body = new HttpErrorResponseBody(
                httpStatus, "Invalid email or password", HttpErrorTypeEnum.AUTHENTICATION_ERROR, Instant.now());

        return ResponseEntity.status(httpStatus).body(body);

    }

    @ExceptionHandler(CredentialAlreadyCreatedException.class)
    public ResponseEntity<HttpErrorResponseBody> handleCredentialAlreadyCreatedException(
            CredentialAlreadyCreatedException e) {

        int httpStatus = HttpStatus.CONFLICT.value();
        HttpErrorResponseBody body = new HttpErrorResponseBody(
                httpStatus, e.getMessage(), HttpErrorTypeEnum.REGISTRATION_ERROR, Instant.now());

        return ResponseEntity.status(httpStatus).body(body);

    }

    // WARN This handler treats only InternalServerErrorExceptions. It works fine for
    // these explicitly thrown exceptions, but spring security's default response
    // for uncaught exceptions is 409 if I remember correctly. I should consider
    // looking into overriding it to a 500 INTERNAL_SERVER_ERROR maybe?
    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<HttpErrorResponseBody> handleInternalServerErrorException(InternalServerErrorException e) {

        int httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
        HttpErrorResponseBody body = new HttpErrorResponseBody(
                httpStatus, "An internal error happened.", HttpErrorTypeEnum.INTERNAL_SERVER_ERROR, Instant.now());

        return ResponseEntity.status(httpStatus).body(body);

    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<HttpErrorResponseBody> handleInvalidRefreshTokenException(InvalidRefreshTokenException e) {
        int httpStatus = HttpStatus.UNAUTHORIZED.value();
        HttpErrorResponseBody body = new HttpErrorResponseBody(
                httpStatus, "Refresh token is invalid", HttpErrorTypeEnum.INVALID_REFRESH_TOKEN, Instant.now());

        return ResponseEntity.status(httpStatus).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpMultipleErrorResponseBody> handleBeanValidationException(MethodArgumentNotValidException e) {

        Map<String, String> messages = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            messages.putIfAbsent(error.getField(), error.getDefaultMessage());
        }

        int httpStatus = HttpStatus.BAD_REQUEST.value();
        HttpMultipleErrorResponseBody body  = new HttpMultipleErrorResponseBody(
            httpStatus, Instant.now(), HttpErrorTypeEnum.VALIDATION_ERROR, messages);

        return ResponseEntity.status(httpStatus).body(body);
    }
}
