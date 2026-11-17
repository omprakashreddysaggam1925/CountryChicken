package com.countrychicken.common.exception;

import com.countrychicken.common.response.ApiError;
import com.countrychicken.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        return response(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", exception.getMessage(), request);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(
            ConflictException exception,
            HttpServletRequest request
    ) {
        return response(HttpStatus.CONFLICT, "CONFLICT", exception.getMessage(), request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(
            BadCredentialsException exception,
            HttpServletRequest request
    ) {
        return response(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid email or password", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<ApiError> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ApiError(
                        "VALIDATION_ERROR",
                        error.getDefaultMessage(),
                        error.getField()
                ))
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure("Request validation failed", errors, request.getRequestURI()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        List<ApiError> errors = exception.getConstraintViolations()
                .stream()
                .map(violation -> new ApiError(
                        "VALIDATION_ERROR",
                        violation.getMessage(),
                        violation.getPropertyPath().toString()
                ))
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure("Request validation failed", errors, request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(
            Exception exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                request
        );
    }

    private ResponseEntity<ApiResponse<Void>> response(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request
    ) {
        ApiError error = new ApiError(code, message, null);
        return ResponseEntity.status(status)
                .body(ApiResponse.failure(message, List.of(error), request.getRequestURI()));
    }
}
