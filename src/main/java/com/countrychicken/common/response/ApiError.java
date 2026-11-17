package com.countrychicken.common.response;

public record ApiError(
        String code,
        String message,
        String field
) {
}
