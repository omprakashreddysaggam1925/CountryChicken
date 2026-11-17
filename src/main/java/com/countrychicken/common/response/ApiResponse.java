package com.countrychicken.common.response;

import java.time.Instant;
import java.util.List;

public record ApiResponse<T>(
        boolean success,
        T data,
        String message,
        List<ApiError> errors,
        Instant timestamp,
        String path
) {

    public static <T> ApiResponse<T> success(T data, String message, String path) {
        return new ApiResponse<>(true, data, message, List.of(), Instant.now(), path);
    }

    public static ApiResponse<Void> failure(String message, List<ApiError> errors, String path) {
        return new ApiResponse<>(false, null, message, errors, Instant.now(), path);
    }
}
