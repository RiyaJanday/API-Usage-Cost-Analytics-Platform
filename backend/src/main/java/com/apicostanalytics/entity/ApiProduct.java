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

@Entity
@Table(name = "apis")
public class ApiProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String baseUrl;

    protected ApiProduct() {
    }

    public ApiProduct(Provider provider, String name, String baseUrl) {
        this.provider = provider;
        this.name = name;
        this.baseUrl = baseUrl;
    }

    public Long getId() {
        return id;
    }

    public Provider getProvider() {
        return provider;
    }

    public String getName() {
        return name;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
