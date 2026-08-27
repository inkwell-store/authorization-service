package com.wilson.inkwell.authorization.dto;

import java.time.Instant;
import java.util.Map;

import com.wilson.inkwell.authorization.enums.HttpErrorTypeEnum;

public record HttpMultipleErrorResponseBody(
        int status, Instant timestamp,  HttpErrorTypeEnum error, Map<String, String> messages) {
}
