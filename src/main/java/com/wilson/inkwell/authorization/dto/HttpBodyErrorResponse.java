package com.wilson.inkwell.authorization.dto;

import java.time.Instant;

public record HttpBodyErrorResponse(
        int status, String message, String error, Instant timestamp) {

}
