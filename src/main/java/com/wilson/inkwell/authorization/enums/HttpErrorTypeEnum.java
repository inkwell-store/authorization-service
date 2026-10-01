package com.wilson.inkwell.authorization.enums;

/**
 * HttpErrorTypeEnum maps errors thrown during from http requests.
 */
public enum HttpErrorTypeEnum {

    /**
     * An error that happens during an authentication flow, i.e the emission of an
     * access and refresh tokens.
     */
    AUTHENTICATION_ERROR,

    /**
     * These are errors that happen inside the application that are either not
     * mapped or unrecoverable.
     */
    INTERNAL_SERVER_ERROR,

    /**
     * Error that happens when a registration attempt fails.
     */
    REGISTRATION_ERROR,

    /**
     * Error that happens when fields on a request body are invalid.
     */
    VALIDATION_ERROR,

    /**
     * The refresh token sent in the request is invalid, like malformed, expired
     * or missing.
     */
    INVALID_REFRESH_TOKEN
}
