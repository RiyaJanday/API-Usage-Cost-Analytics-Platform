package com.apicostanalytics.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record AlertResponse(Long id, String type, BigDecimal threshold, BigDecimal currentValue, String status, Instant createdAt) {
}
