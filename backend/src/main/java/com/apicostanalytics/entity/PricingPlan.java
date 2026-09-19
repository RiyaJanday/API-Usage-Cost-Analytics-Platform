package com.apicostanalytics.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "pricing_plans")
public class PricingPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false, precision = 12, scale = 8)
    private BigDecimal inputPricePerUnit;

    @Column(nullable = false, precision = 12, scale = 8)
    private BigDecimal outputPricePerUnit;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private Instant effectiveFrom;

    private Instant effectiveTo;

    protected PricingPlan() {
    }

    public PricingPlan(Provider provider, String model, BigDecimal inputPricePerUnit, BigDecimal outputPricePerUnit,
            String currency, Instant effectiveFrom, Instant effectiveTo) {
        this.provider = provider;
        this.model = model;
        this.inputPricePerUnit = inputPricePerUnit;
        this.outputPricePerUnit = outputPricePerUnit;
        this.currency = currency;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
    }

    public Long getId() {
        return id;
    }

    public Provider getProvider() {
        return provider;
    }

    public String getModel() {
        return model;
    }

    public BigDecimal getInputPricePerUnit() {
        return inputPricePerUnit;
    }

    public BigDecimal getOutputPricePerUnit() {
        return outputPricePerUnit;
    }

    public String getCurrency() {
        return currency;
    }

    public Instant getEffectiveFrom() {
        return effectiveFrom;
    }

    public Instant getEffectiveTo() {
        return effectiveTo;
    }
}
