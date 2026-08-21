package com.example.foodchain.common.error;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ApiException {
    public ForbiddenException(String message) {
        super("FORBIDDEN", HttpStatus.FORBIDDEN, message);
    }
}
