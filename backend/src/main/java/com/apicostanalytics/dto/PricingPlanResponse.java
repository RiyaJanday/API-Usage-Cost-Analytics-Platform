package com.apicostanalytics.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PricingPlanResponse(
        Long id,
        String provider,
        String model,
        BigDecimal inputPricePerUnit,
        BigDecimal outputPricePerUnit,
        String currency,
        Instant effectiveFrom,
        Instant effectiveTo) {
}
