package com.apicostanalytics.service;

import com.apicostanalytics.dto.AlertResponse;
import com.apicostanalytics.dto.DashboardResponse;
import com.apicostanalytics.entity.Alert;
import com.apicostanalytics.repository.AlertRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final AnalyticsService analyticsService;
    private final AlertRepository alertRepository;

    public DashboardService(AnalyticsService analyticsService, AlertRepository alertRepository) {
        this.analyticsService = analyticsService;
        this.alertRepository = alertRepository;
    }

    public DashboardResponse dashboard(int days) {
        return new DashboardResponse(
                analyticsService.summary(days),
                analyticsService.costByProvider(days),
                analyticsService.topEndpoints(days),
                analyticsService.dailyTrend(days),
                alertRepository.findAll().stream().map(this::toResponse).toList());
    }

    private AlertResponse toResponse(Alert alert) {
        return new AlertResponse(
                alert.getId(),
                alert.getType().name(),
                alert.getThreshold(),
                alert.getCurrentValue(),
                alert.getStatus().name(),
                alert.getCreatedAt());
    }
}
