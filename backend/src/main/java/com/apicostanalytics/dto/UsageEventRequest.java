package com.apicostanalytics.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record UsageEventRequest(
        @NotNull Long apiId,
        @NotNull Long endpointId,
        @NotNull Long userId,
        @NotBlank String provider,
        @NotBlank String model,
        @NotBlank String endpoint,
        @Min(100) int statusCode,
        @Min(0) long latencyMs,
        @Min(0) long inputUnits,
        @Min(0) long outputUnits,
        Instant timestamp) {
}
