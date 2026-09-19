package com.apicostanalytics.repository;

import com.apicostanalytics.entity.Endpoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EndpointRepository extends JpaRepository<Endpoint, Long> {
}
