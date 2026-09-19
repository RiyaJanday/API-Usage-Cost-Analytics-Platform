package com.apicostanalytics.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record UsageEventResponse(
        Long apiId,
        Long endpointId,
        Long userId,
        String provider,
        String model,
        String endpoint,
        int statusCode,
        long latencyMs,
        long inputUnits,
        long outputUnits,
        BigDecimal cost,
        Instant timestamp) {
}
