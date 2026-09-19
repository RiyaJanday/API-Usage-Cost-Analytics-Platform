package com.apicostanalytics.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TrendPoint(LocalDate day, long requests, BigDecimal cost, double errorRate, double averageLatencyMs) {
}
