package com.wilson.inkwell.authorization.enums;

/**
 * Enum that represents the possible states of a Refresh Token
 */
public enum RefreshTokenStatusEnum {
    /** Token was revoked manually by the user. Makes it invalid. */
    REVOKED,

    /** Token is expired. Makes it invalid. */
    EXPIRED,

    /** Token is valid, meaning not expired and not revoked. */
    VALID
}
