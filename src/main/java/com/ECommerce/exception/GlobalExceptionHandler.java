package com.ECommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Catch custom exceptions
//    @ExceptionHandler(CustomException.class)
//    public ResponseEntity<ApiError> handleCustom(CustomException ex) {
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(new ApiError(ex.getMessage()));
//    }

    // Catch validation errors (@Valid)
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
//        String message = ex.getBindingResult()
//                .getFieldErrors()
//                .get(0)
//                .getDefaultMessage();
//
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(new ApiError(message));
//    }

    // Catch authentication errors
//    @ExceptionHandler(AuthenticationException.class)
//    public ResponseEntity<ApiError> handleAuth(AuthenticationException ex) {
//        return ResponseEntity
//                .status(HttpStatus.UNAUTHORIZED)
//                .body(new ApiError("Invalid email or password"));
//    }

    // Catch ANY unexpected exception (fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("Something went wrong"));
    }
}

