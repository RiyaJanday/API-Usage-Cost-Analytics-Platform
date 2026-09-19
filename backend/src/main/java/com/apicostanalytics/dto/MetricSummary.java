package com.apicostanalytics.dto;

import java.math.BigDecimal;

public record MetricSummary(long totalRequests, BigDecimal totalCost, double errorRate, double averageLatencyMs) {
}
