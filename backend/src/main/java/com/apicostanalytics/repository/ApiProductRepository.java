package com.apicostanalytics.repository;

import com.apicostanalytics.entity.ApiProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiProductRepository extends JpaRepository<ApiProduct, Long> {
}
