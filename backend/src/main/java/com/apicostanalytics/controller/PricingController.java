package com.apicostanalytics.controller;

import com.apicostanalytics.dto.PricingPlanResponse;
import com.apicostanalytics.entity.PricingPlan;
import com.apicostanalytics.repository.PricingPlanRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {
    private final PricingPlanRepository pricingPlanRepository;

    public PricingController(PricingPlanRepository pricingPlanRepository) {
        this.pricingPlanRepository = pricingPlanRepository;
    }

    @GetMapping
    public List<PricingPlanResponse> pricing() {
        return pricingPlanRepository.findAll().stream().map(this::toResponse).toList();
    }

    private PricingPlanResponse toResponse(PricingPlan plan) {
        return new PricingPlanResponse(
                plan.getId(),
                plan.getProvider().getName(),
                plan.getModel(),
                plan.getInputPricePerUnit(),
                plan.getOutputPricePerUnit(),
                plan.getCurrency(),
                plan.getEffectiveFrom(),
                plan.getEffectiveTo());
    }
}
