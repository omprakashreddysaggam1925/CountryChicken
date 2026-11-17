package com.countrychicken.review.dto;

import jakarta.validation.constraints.*;

public record ReviewRequest(@Min(1) @Max(5) int rating, @Size(max = 2000) String comment) {}
