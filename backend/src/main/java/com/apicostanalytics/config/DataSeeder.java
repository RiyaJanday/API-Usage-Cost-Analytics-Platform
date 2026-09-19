package com.apicostanalytics.config;

import com.apicostanalytics.dto.UsageEventRequest;
import com.apicostanalytics.entity.Alert;
import com.apicostanalytics.entity.ApiProduct;
import com.apicostanalytics.entity.Endpoint;
import com.apicostanalytics.entity.Organization;
import com.apicostanalytics.entity.PricingPlan;
import com.apicostanalytics.entity.Provider;
import com.apicostanalytics.entity.UserAccount;
import com.apicostanalytics.repository.AlertRepository;
import com.apicostanalytics.repository.ApiProductRepository;
import com.apicostanalytics.repository.EndpointRepository;
import com.apicostanalytics.repository.OrganizationRepository;
import com.apicostanalytics.repository.PricingPlanRepository;
import com.apicostanalytics.repository.ProviderRepository;
import com.apicostanalytics.repository.UserAccountRepository;
import com.apicostanalytics.service.UsageJdbcRepository;
import com.apicostanalytics.service.UsageService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seedData(
            UsageJdbcRepository usageJdbcRepository,
            OrganizationRepository organizationRepository,
            UserAccountRepository userAccountRepository,
            ProviderRepository providerRepository,
            ApiProductRepository apiProductRepository,
            EndpointRepository endpointRepository,
            PricingPlanRepository pricingPlanRepository,
            AlertRepository alertRepository,
            UsageService usageService) {
        return args -> seed(
                usageJdbcRepository,
                organizationRepository,
                userAccountRepository,
                providerRepository,
                apiProductRepository,
                endpointRepository,
                pricingPlanRepository,
                alertRepository,
                usageService);
    }

    @Transactional
    void seed(
            UsageJdbcRepository usageJdbcRepository,
            OrganizationRepository organizationRepository,
            UserAccountRepository userAccountRepository,
            ProviderRepository providerRepository,
            ApiProductRepository apiProductRepository,
            EndpointRepository endpointRepository,
            PricingPlanRepository pricingPlanRepository,
            AlertRepository alertRepository,
            UsageService usageService) {
        usageJdbcRepository.createUsageTable();

        Organization organization = organizationRepository.save(new Organization("Acme API Platform"));
        UserAccount user = userAccountRepository.save(new UserAccount(
                "Riya Sharma",
                "riya@example.com",
                "{noop}demo",
                UserAccount.Role.ADMIN,
                organization));

        Provider openai = providerRepository.save(new Provider("OpenAI", "AI", "ACTIVE"));
        Provider anthropic = providerRepository.save(new Provider("Anthropic", "AI", "ACTIVE"));
        Provider gemini = providerRepository.save(new Provider("Gemini", "AI", "ACTIVE"));
        Provider stripe = providerRepository.save(new Provider("Stripe", "Payments", "ACTIVE"));

        ApiProduct chatApi = apiProductRepository.save(new ApiProduct(openai, "Chat API", "https://api.openai.com"));
        ApiProduct messagesApi = apiProductRepository.save(new ApiProduct(anthropic, "Messages API", "https://api.anthropic.com"));
        ApiProduct generateApi = apiProductRepository.save(new ApiProduct(gemini, "Generate API", "https://generativelanguage.googleapis.com"));
        ApiProduct paymentApi = apiProductRepository.save(new ApiProduct(stripe, "Payments API", "https://api.stripe.com"));

        Endpoint chatCompletions = endpointRepository.save(new Endpoint(chatApi, "/v1/chat/completions", "POST"));
        Endpoint messages = endpointRepository.save(new Endpoint(messagesApi, "/v1/messages", "POST"));
        Endpoint generate = endpointRepository.save(new Endpoint(generateApi, "/v1beta/models/gemini-pro:generateContent", "POST"));
        Endpoint paymentIntents = endpointRepository.save(new Endpoint(paymentApi, "/v1/payment_intents", "POST"));

        Instant effective = Instant.now().minus(90, ChronoUnit.DAYS);
        pricingPlanRepository.save(new PricingPlan(openai, "gpt-5", new BigDecimal("0.000010"), new BigDecimal("0.000030"), "USD", effective, null));
        pricingPlanRepository.save(new PricingPlan(anthropic, "claude-4", new BigDecimal("0.000008"), new BigDecimal("0.000024"), "USD", effective, null));
        pricingPlanRepository.save(new PricingPlan(gemini, "gemini-pro", new BigDecimal("0.000004"), new BigDecimal("0.000012"), "USD", effective, null));
        pricingPlanRepository.save(new PricingPlan(stripe, "payments", new BigDecimal("0.000000"), new BigDecimal("0.000000"), "USD", effective, null));

        alertRepository.save(new Alert(organization, Alert.AlertType.ANOMALY, new BigDecimal("150.00"), new BigDecimal("188.00"), Alert.Status.TRIGGERED));
        alertRepository.save(new Alert(organization, Alert.AlertType.ERROR_RATE, new BigDecimal("5.00"), new BigDecimal("2.41"), Alert.Status.ACTIVE));
        alertRepository.save(new Alert(organization, Alert.AlertType.DAILY_COST, new BigDecimal("50.00"), new BigDecimal("42.82"), Alert.Status.ACTIVE));

        List<UsageEventRequest> events = new ArrayList<>();
        Random random = new Random(42);
        List<SeedEndpoint> endpoints = List.of(
                new SeedEndpoint(openai, chatApi, chatCompletions, "gpt-5", 52),
                new SeedEndpoint(anthropic, messagesApi, messages, "claude-4", 25),
                new SeedEndpoint(gemini, generateApi, generate, "gemini-pro", 17),
                new SeedEndpoint(stripe, paymentApi, paymentIntents, "payments", 6));

        for (int day = 29; day >= 0; day--) {
            for (SeedEndpoint seedEndpoint : endpoints) {
                int requestCount = 14 + random.nextInt(18) + seedEndpoint.weight() / 4;
                for (int i = 0; i < requestCount; i++) {
                    int statusCode = random.nextDouble() < 0.024 ? 500 : 200;
                    long input = seedEndpoint.provider().getName().equals("Stripe") ? 0 : 400 + random.nextInt(2600);
                    long output = seedEndpoint.provider().getName().equals("Stripe") ? 0 : 160 + random.nextInt(1100);
                    long latency = seedEndpoint.provider().getName().equals("Stripe")
                            ? 70 + random.nextInt(120)
                            : 120 + random.nextInt(880);
                    Instant timestamp = Instant.now()
                            .minus(day, ChronoUnit.DAYS)
                            .minus(random.nextInt(86_400), ChronoUnit.SECONDS);
                    events.add(new UsageEventRequest(
                            seedEndpoint.api().getId(),
                            seedEndpoint.endpoint().getId(),
                            user.getId(),
                            seedEndpoint.provider().getName(),
                            seedEndpoint.model(),
                            seedEndpoint.endpoint().getPath(),
                            statusCode,
                            latency,
                            input,
                            output,
                            timestamp));
                }
            }
        }
        usageService.ingest(events);
    }

    private record SeedEndpoint(Provider provider, ApiProduct api, Endpoint endpoint, String model, int weight) {
    }
}
