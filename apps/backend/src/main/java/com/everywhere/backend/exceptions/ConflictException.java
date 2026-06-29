package com.everywhere.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {
    private String instance;

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, String instance) {
        super(message);
        this.instance = instance;
    }

    public String getInstance() {
        return instance;
    }
}