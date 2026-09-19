package com.apicostanalytics.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "alerts")
public class Alert {
    public enum AlertType {
        DAILY_COST, ERROR_RATE, ANOMALY
    }

    public enum Status {
        ACTIVE, TRIGGERED, MUTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType type;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal threshold;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal currentValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected Alert() {
    }

    public Alert(Organization organization, AlertType type, BigDecimal threshold, BigDecimal currentValue, Status status) {
        this.organization = organization;
        this.type = type;
        this.threshold = threshold;
        this.currentValue = currentValue;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public AlertType getType() {
        return type;
    }

    public BigDecimal getThreshold() {
        return threshold;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public Status getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
