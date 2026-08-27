package com.wilson.inkwell.authorization.dto;

import java.time.Instant;

import com.wilson.inkwell.authorization.enums.HttpErrorTypeEnum;

public record HttpErrorResponseBody(
    int status, String message, HttpErrorTypeEnum error, Instant timestamp) {
    
}
