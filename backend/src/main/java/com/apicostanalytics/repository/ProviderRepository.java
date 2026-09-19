package com.apicostanalytics.repository;

import com.apicostanalytics.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRepository extends JpaRepository<Provider, Long> {
}
