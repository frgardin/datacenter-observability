package com.observability.scheduled;

import com.observability.config.ObservabilityConfig;
import com.observability.dto.MetricsDTO;
import com.observability.service.MetricsCollectionService;
import com.observability.service.HistoricalMetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MetricsCollectionScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MetricsCollectionScheduler.class);

    @Autowired
    private MetricsCollectionService metricsCollectionService;

    @Autowired
    private HistoricalMetricsService historicalMetricsService;

    @Autowired
    private ObservabilityConfig config;

    @Scheduled(fixedRate = 60000) // Collect metrics every minute
    public void collectAndStoreMetrics() {
        logger.debug("Starting scheduled metrics collection");
        
        for (ObservabilityConfig.TargetConfig target : config.getTargets()) {
            try {
                MetricsDTO metrics = metricsCollectionService.collectMetrics(target);
                if (metrics != null) {
                    historicalMetricsService.saveMetrics(metrics);
                    logger.debug("Successfully collected and stored metrics for target: {}", target.getName());
                } else {
                    logger.warn("Failed to collect metrics for target: {}", target.getName());
                }
            } catch (Exception e) {
                logger.error("Error collecting metrics for target {}: {}", target.getName(), e.getMessage());
            }
        }
        
        logger.debug("Completed scheduled metrics collection");
    }
}
