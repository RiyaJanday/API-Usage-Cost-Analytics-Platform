package com.apicostanalytics.controller;

import com.apicostanalytics.dto.UsageEventRequest;
import com.apicostanalytics.dto.UsageEventResponse;
import com.apicostanalytics.service.UsageService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/usage")
public class UsageController {
    private final UsageService usageService;

    public UsageController(UsageService usageService) {
        this.usageService = usageService;
    }

    @GetMapping
    public List<UsageEventResponse> recent(@RequestParam(defaultValue = "50") int limit) {
        return usageService.recent(Math.min(limit, 250));
    }

    @PostMapping
    public List<UsageEventResponse> ingest(@Valid @RequestBody List<UsageEventRequest> events) {
        return usageService.ingest(events);
    }
}
