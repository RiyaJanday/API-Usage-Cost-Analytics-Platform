package com.apicostanalytics.dto;

import java.math.BigDecimal;

public record BreakdownPoint(String label, long requests, BigDecimal cost) {
}
