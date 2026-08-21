package com.example.foodchain.common.error;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class BadRequestException extends ApiException {
    public BadRequestException(String message) {
        super("BAD_REQUEST", HttpStatus.BAD_REQUEST, message);
    }

    public BadRequestException(String code, String message, Map<String, Object> details) {
        super(code, HttpStatus.BAD_REQUEST, message, details);
    }
}
