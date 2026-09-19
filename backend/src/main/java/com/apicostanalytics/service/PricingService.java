package com.apicostanalytics.service;

import com.apicostanalytics.entity.PricingPlan;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PricingService {
    private final JdbcTemplate jdbcTemplate;

    public PricingService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public BigDecimal calculateCost(String provider, String model, long inputUnits, long outputUnits, Instant timestamp) {
        String sql = """
                select pp.input_price_per_unit, pp.output_price_per_unit
                from pricing_plans pp
                join providers p on p.id = pp.provider_id
                where lower(p.name) = lower(?)
                  and lower(pp.model) = lower(?)
                  and pp.effective_from <= ?
                  and (pp.effective_to is null or pp.effective_to > ?)
                order by pp.effective_from desc
                limit 1
                """;

        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) {
                return BigDecimal.ZERO;
            }
            BigDecimal inputPrice = rs.getBigDecimal("input_price_per_unit");
            BigDecimal outputPrice = rs.getBigDecimal("output_price_per_unit");
            return inputPrice.multiply(BigDecimal.valueOf(inputUnits))
                    .add(outputPrice.multiply(BigDecimal.valueOf(outputUnits)))
                    .setScale(6, RoundingMode.HALF_UP);
        }, provider, model, timestamp, timestamp);
    }
}
