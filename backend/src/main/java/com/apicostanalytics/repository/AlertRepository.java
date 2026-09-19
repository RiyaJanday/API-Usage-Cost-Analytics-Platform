package com.apicostanalytics.repository;

import com.apicostanalytics.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {
}
