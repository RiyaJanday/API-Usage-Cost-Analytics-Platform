package com.apicostanalytics.controller;

import com.apicostanalytics.dto.ProviderResponse;
import com.apicostanalytics.entity.Provider;
import com.apicostanalytics.repository.ProviderRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/providers")
public class ProviderController {
    private final ProviderRepository providerRepository;

    public ProviderController(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @GetMapping
    public List<ProviderResponse> providers() {
        return providerRepository.findAll().stream().map(this::toResponse).toList();
    }

    private ProviderResponse toResponse(Provider provider) {
        return new ProviderResponse(provider.getId(), provider.getName(), provider.getType(), provider.getStatus());
    }
}
