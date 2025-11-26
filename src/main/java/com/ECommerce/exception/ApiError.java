package com.ECommerce.exception;


import java.time.LocalDateTime;

public class ApiError {
    private boolean success = false;
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();

    public ApiError(String message) {
        this.message = message;
    }
}

