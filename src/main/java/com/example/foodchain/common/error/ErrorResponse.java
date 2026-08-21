package com.example.foodchain.common.error;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Uniform error payload returned by every failing endpoint.
 * Shape: {code, message, details, timestamp}.
 */
public record ErrorResponse(
        String code,
        String message,
        Map<String, Object> details,
        OffsetDateTime timestamp
) {
    public static ErrorResponse of(String code, String message, Map<String, Object> details) {
        return new ErrorResponse(code, message, details == null ? Map.of() : details, OffsetDateTime.now());
    }
}
