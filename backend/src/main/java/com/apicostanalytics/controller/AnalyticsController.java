package com.apicostanalytics.controller;

import com.apicostanalytics.dto.BreakdownPoint;
import com.apicostanalytics.dto.MetricSummary;
import com.apicostanalytics.dto.TrendPoint;
import com.apicostanalytics.service.AnalyticsService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    public MetricSummary summary(@RequestParam(defaultValue = "30") int days) {
        return analyticsService.summary(days);
    }

    @GetMapping("/cost")
    public List<BreakdownPoint> cost(@RequestParam(defaultValue = "30") int days) {
        return analyticsService.costByProvider(days);
    }

    @GetMapping("/requests")
    public List<TrendPoint> requests(@RequestParam(defaultValue = "30") int days) {
        return analyticsService.dailyTrend(days);
    }

    @GetMapping("/errors")
    public List<TrendPoint> errors(@RequestParam(defaultValue = "30") int days) {
        return analyticsService.dailyTrend(days);
    }

    @GetMapping("/latency")
    public List<TrendPoint> latency(@RequestParam(defaultValue = "30") int days) {
        return analyticsService.dailyTrend(days);
    }
}
