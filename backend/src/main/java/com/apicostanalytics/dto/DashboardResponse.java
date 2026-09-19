package com.apicostanalytics.dto;

import java.util.List;

public record DashboardResponse(
        MetricSummary summary,
        List<BreakdownPoint> costByProvider,
        List<BreakdownPoint> topEndpoints,
        List<TrendPoint> dailyTrend,
        List<AlertResponse> alerts) {
}
