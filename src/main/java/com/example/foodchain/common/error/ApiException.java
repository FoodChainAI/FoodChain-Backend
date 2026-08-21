package com.example.foodchain.common.error;

import java.util.Map;
import org.springframework.http.HttpStatus;

/**
 * Base class for all business/HTTP exceptions. Carries a stable error {@code code},
 * an HTTP status and optional structured details.
 */
public class ApiException extends RuntimeException {

    private final String code;
    private final HttpStatus status;
    private final transient Map<String, Object> details;

    public ApiException(String code, HttpStatus status, String message, Map<String, Object> details) {
        super(message);
        this.code = code;
        this.status = status;
        this.details = details == null ? Map.of() : details;
    }

    public ApiException(String code, HttpStatus status, String message) {
        this(code, status, message, Map.of());
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
