package com.apicostanalytics.controller;

import com.apicostanalytics.dto.DashboardResponse;
import com.apicostanalytics.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardResponse dashboard(@RequestParam(defaultValue = "30") int days) {
        return dashboardService.dashboard(days);
    }
}
