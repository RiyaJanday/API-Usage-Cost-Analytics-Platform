package com.apicostanalytics.controller;

import com.apicostanalytics.dto.AlertResponse;
import com.apicostanalytics.entity.Alert;
import com.apicostanalytics.repository.AlertRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    private final AlertRepository alertRepository;

    public AlertController(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @GetMapping
    public List<AlertResponse> alerts() {
        return alertRepository.findAll().stream().map(this::toResponse).toList();
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
