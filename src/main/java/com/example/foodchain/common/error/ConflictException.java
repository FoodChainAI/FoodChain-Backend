package com.example.foodchain.common.error;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class ConflictException extends ApiException {
    public ConflictException(String message) {
        super("CONFLICT", HttpStatus.CONFLICT, message);
    }

    public ConflictException(String code, String message, Map<String, Object> details) {
        super(code, HttpStatus.CONFLICT, message, details);
    }
}
