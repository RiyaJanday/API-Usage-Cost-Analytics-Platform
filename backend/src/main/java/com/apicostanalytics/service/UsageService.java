package com.apicostanalytics.service;

import com.apicostanalytics.dto.UsageEventRequest;
import com.apicostanalytics.dto.UsageEventResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsageService {
    private final PricingService pricingService;
    private final UsageJdbcRepository usageJdbcRepository;

    public UsageService(PricingService pricingService, UsageJdbcRepository usageJdbcRepository) {
        this.pricingService = pricingService;
        this.usageJdbcRepository = usageJdbcRepository;
    }

    @Transactional
    public List<UsageEventResponse> ingest(List<UsageEventRequest> requests) {
        List<UsageEventResponse> events = requests.stream()
                .map(this::toEvent)
                .toList();
        usageJdbcRepository.batchInsert(events);
        return events;
    }

    public List<UsageEventResponse> recent(int limit) {
        return usageJdbcRepository.findRecent(limit);
    }

    private UsageEventResponse toEvent(UsageEventRequest request) {
        Instant timestamp = request.timestamp() == null ? Instant.now() : request.timestamp();
        BigDecimal cost = pricingService.calculateCost(
                request.provider(),
                request.model(),
                request.inputUnits(),
                request.outputUnits(),
                timestamp);
        return new UsageEventResponse(
                request.apiId(),
                request.endpointId(),
                request.userId(),
                request.provider(),
                request.model(),
                request.endpoint(),
                request.statusCode(),
                request.latencyMs(),
                request.inputUnits(),
                request.outputUnits(),
                cost,
                timestamp);
    }
}
