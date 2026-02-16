package com.observability.controller;

import com.observability.dto.MetricsDTO;
import com.observability.service.MetricsCollectionService;
import com.observability.service.HistoricalMetricsService;
import com.observability.config.ObservabilityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MetricsController {

    @Autowired
    private MetricsCollectionService metricsCollectionService;
    
    @Autowired
    private HistoricalMetricsService historicalMetricsService;
    
    @Autowired
    private ObservabilityConfig config;

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, MetricsDTO>> getCurrentMetrics() {
        Map<String, MetricsDTO> allMetrics = new HashMap<>();
        
        for (ObservabilityConfig.TargetConfig target : config.getTargets()) {
            MetricsDTO metrics = metricsCollectionService.collectMetrics(target);
            if (metrics != null) {
                allMetrics.put(target.getName(), metrics);
            }
        }
        
        return ResponseEntity.ok(allMetrics);
    }

    @GetMapping("/metrics/{target}")
    public ResponseEntity<MetricsDTO> getTargetMetrics(@PathVariable String target) {
        for (ObservabilityConfig.TargetConfig targetConfig : config.getTargets()) {
            if (targetConfig.getName().equals(target)) {
                MetricsDTO metrics = metricsCollectionService.collectMetrics(targetConfig);
                if (metrics != null) {
                    return ResponseEntity.ok(metrics);
                }
                break;
            }
        }
        
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/targets")
    public ResponseEntity<List<TargetStatus>> getTargets() {
        List<TargetStatus> targets = new java.util.ArrayList<>();
        
        for (ObservabilityConfig.TargetConfig targetConfig : config.getTargets()) {
            TargetStatus status = new TargetStatus();
            status.setName(targetConfig.getName());
            status.setHost(targetConfig.getHost());
            status.setTags(targetConfig.getTags());
            
            // Check if target is responsive
            MetricsDTO metrics = metricsCollectionService.collectMetrics(targetConfig);
            status.setStatus(metrics != null ? "online" : "offline");
            
            targets.add(status);
        }
        
        return ResponseEntity.ok(targets);
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<Alert>> getAlerts() {
        List<Alert> alerts = new java.util.ArrayList<>();
        ObservabilityConfig.AlertsConfig thresholds = config.getAlerts();
        
        if (thresholds == null) {
            return ResponseEntity.ok(alerts);
        }
        
        for (ObservabilityConfig.TargetConfig targetConfig : config.getTargets()) {
            MetricsDTO metrics = metricsCollectionService.collectMetrics(targetConfig);
            if (metrics == null) continue;
            
            String targetName = targetConfig.getName();
            
            // Check CPU alerts
            if (metrics.getCpu() != null && thresholds.getCpu() != null) {
                double cpuUsage = metrics.getCpu().getUsagePercent();
                if (cpuUsage > thresholds.getCpu().getCritical()) {
                    alerts.add(new Alert(targetName, "cpu", "critical", cpuUsage, thresholds.getCpu().getCritical()));
                } else if (cpuUsage > thresholds.getCpu().getWarning()) {
                    alerts.add(new Alert(targetName, "cpu", "warning", cpuUsage, thresholds.getCpu().getWarning()));
                }
            }
            
            // Check memory alerts
            if (metrics.getMemory() != null && thresholds.getRam() != null) {
                double memUsage = metrics.getMemory().getUsagePercent();
                if (memUsage > thresholds.getRam().getCritical()) {
                    alerts.add(new Alert(targetName, "memory", "critical", memUsage, thresholds.getRam().getCritical()));
                } else if (memUsage > thresholds.getRam().getWarning()) {
                    alerts.add(new Alert(targetName, "memory", "warning", memUsage, thresholds.getRam().getWarning()));
                }
            }
            
            // Check disk alerts
            if (metrics.getDisk() != null && thresholds.getDisk() != null) {
                for (MetricsDTO.DiskMetricsDTO disk : metrics.getDisk()) {
                    int diskUsage = disk.getUsagePercent();
                    if (diskUsage > thresholds.getDisk().getCritical()) {
                        alerts.add(new Alert(targetName, "disk", "critical", diskUsage, thresholds.getDisk().getCritical(), disk.getMountPoint()));
                    } else if (diskUsage > thresholds.getDisk().getWarning()) {
                        alerts.add(new Alert(targetName, "disk", "warning", diskUsage, thresholds.getDisk().getWarning(), disk.getMountPoint()));
                    }
                }
            }
        }
        
        return ResponseEntity.ok(alerts);
    }

    // Historical data endpoints
    @GetMapping("/metrics/{target}/history")
    public ResponseEntity<List<MetricsDTO>> getTargetHistory(
            @PathVariable String target,
            @RequestParam(defaultValue = "24") int hoursBack) {
        List<MetricsDTO> history = historicalMetricsService.getRecentMetrics(target, hoursBack);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/metrics/{target}/history/range")
    public ResponseEntity<List<MetricsDTO>> getTargetHistoryRange(
            @PathVariable String target,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<MetricsDTO> history = historicalMetricsService.getHistoricalMetrics(target, startTime, endTime);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/metrics/history")
    public ResponseEntity<Map<String, List<MetricsDTO>>> getAllTargetsHistory(
            @RequestParam(defaultValue = "24") int hoursBack) {
        List<MetricsDTO> allHistory = historicalMetricsService.getAllRecentMetrics(hoursBack);
        Map<String, List<MetricsDTO>> historyByTarget = new HashMap<>();
        
        for (MetricsDTO metrics : allHistory) {
            historyByTarget.computeIfAbsent(metrics.getTarget(), k -> new ArrayList<>()).add(metrics);
        }
        
        return ResponseEntity.ok(historyByTarget);
    }

    // DTOs for responses
    public static class TargetStatus {
        private String name;
        private String host;
        private List<String> tags;
        private String status;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class Alert {
        private String target;
        private String type;
        private String level;
        private double value;
        private double threshold;
        private String mountPoint;

        public Alert(String target, String type, String level, double value, double threshold) {
            this(target, type, level, value, threshold, null);
        }

        public Alert(String target, String type, String level, double value, double threshold, String mountPoint) {
            this.target = target;
            this.type = type;
            this.level = level;
            this.value = value;
            this.threshold = threshold;
            this.mountPoint = mountPoint;
        }

        // Getters and Setters
        public String getTarget() { return target; }
        public void setTarget(String target) { this.target = target; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        
        public double getValue() { return value; }
        public void setValue(double value) { this.value = value; }
        
        public double getThreshold() { return threshold; }
        public void setThreshold(double threshold) { this.threshold = threshold; }
        
        public String getMountPoint() { return mountPoint; }
        public void setMountPoint(String mountPoint) { this.mountPoint = mountPoint; }
    }
}
