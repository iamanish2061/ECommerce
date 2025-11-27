package com.ECommerce.dto.response;

public record ApiResponse(
        boolean success,
        String message,
        String errorCode,
        long timestamp
    ) {
    public static ApiResponse ok(String msg) {
        return new ApiResponse(true, msg, null, System.currentTimeMillis());
    }

    public static ApiResponse error(String msg, String code) {
        return new ApiResponse(false, msg, code, System.currentTimeMillis());
    }
}

