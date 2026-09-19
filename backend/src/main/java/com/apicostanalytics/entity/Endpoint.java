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
@Table(name = "endpoints")
public class Endpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_id", nullable = false)
    private ApiProduct api;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String method;

    protected Endpoint() {
    }

    public Endpoint(ApiProduct api, String path, String method) {
        this.api = api;
        this.path = path;
        this.method = method;
    }

    public Long getId() {
        return id;
    }

    public ApiProduct getApi() {
        return api;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }
}
