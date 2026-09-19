package com.apicostanalytics.service;

import com.apicostanalytics.dto.BreakdownPoint;
import com.apicostanalytics.dto.MetricSummary;
import com.apicostanalytics.dto.TrendPoint;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {
    private final JdbcTemplate jdbcTemplate;

    public AnalyticsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public MetricSummary summary(int days) {
        Instant since = Instant.now().minus(days, ChronoUnit.DAYS);
        String sql = """
                select count(*) as requests,
                       coalesce(sum(cost), 0) as cost,
                       coalesce(avg(latency_ms), 0) as latency,
                       coalesce(sum(case when status_code >= 400 then 1 else 0 end) * 100.0 / nullif(count(*), 0), 0) as error_rate
                from api_usage
                where timestamp >= ?
                """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new MetricSummary(
                rs.getLong("requests"),
                rs.getBigDecimal("cost"),
                rs.getDouble("error_rate"),
                rs.getDouble("latency")), since);
    }

    public List<BreakdownPoint> costByProvider(int days) {
        return breakdown("provider", days, 8);
    }

    public List<BreakdownPoint> topEndpoints(int days) {
        return breakdown("endpoint", days, 8);
    }

    public List<TrendPoint> dailyTrend(int days) {
        Instant since = Instant.now().minus(days, ChronoUnit.DAYS);
        return jdbcTemplate.query("""
                select cast(timestamp as date) as usage_day,
                       count(*) as requests,
                       coalesce(sum(cost), 0) as cost,
                       coalesce(avg(latency_ms), 0) as latency,
                       coalesce(sum(case when status_code >= 400 then 1 else 0 end) * 100.0 / nullif(count(*), 0), 0) as error_rate
                from api_usage
                where timestamp >= ?
                group by cast(timestamp as date)
                order by usage_day
                """, (rs, rowNum) -> {
                    Date day = rs.getDate("usage_day");
                    LocalDate localDate = day.toLocalDate();
                    return new TrendPoint(
                            localDate,
                            rs.getLong("requests"),
                            rs.getBigDecimal("cost"),
                            rs.getDouble("error_rate"),
                            rs.getDouble("latency"));
                }, since);
    }

    private List<BreakdownPoint> breakdown(String dimension, int days, int limit) {
        Instant since = Instant.now().minus(days, ChronoUnit.DAYS);
        String sql = """
                select %s as label, count(*) as requests, coalesce(sum(cost), 0) as cost
                from api_usage
                where timestamp >= ?
                group by %s
                order by cost desc
                limit ?
                """.formatted(dimension, dimension);
        return jdbcTemplate.query(sql, (rs, rowNum) -> new BreakdownPoint(
                rs.getString("label"),
                rs.getLong("requests"),
                rs.getBigDecimal("cost")), since, limit);
    }
}
