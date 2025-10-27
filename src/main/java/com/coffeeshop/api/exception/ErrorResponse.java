package com.coffeeshop.api.exception;

import java.time.Instant;
import java.util.Map;

/**
 * RFC 7807 Problem Details for HTTP APIs
 */
public record ErrorResponse(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        Instant timestamp,
        Map<String, Object> additionalProperties) {
    public static ErrorResponse of(
            String type, String title, int status, String detail, String instance) {
        return new ErrorResponse(type, title, status, detail, instance, Instant.now(), null);
    }

    public static ErrorResponse withErrors(
            String type,
            String title,
            int status,
            String detail,
            String instance,
            Map<String, Object> errors) {
        return new ErrorResponse(type, title, status, detail, instance, Instant.now(), errors);
    }
}



