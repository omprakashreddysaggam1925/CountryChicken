package com.countrychicken.common.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    void successResponseContainsDataAndNoErrors() {
        ApiResponse<String> response = ApiResponse.success("value", "Done", "/test");

        assertThat(response.success()).isTrue();
        assertThat(response.data()).isEqualTo("value");
        assertThat(response.errors()).isEmpty();
        assertThat(response.path()).isEqualTo("/test");
    }

    @Test
    void failureResponseContainsErrorsAndNoData() {
        ApiError error = new ApiError("INVALID", "Invalid value", "name");

        ApiResponse<Void> response = ApiResponse.failure(
                "Request failed",
                java.util.List.of(error),
                "/test"
        );

        assertThat(response.success()).isFalse();
        assertThat(response.data()).isNull();
        assertThat(response.errors()).containsExactly(error);
    }
}
