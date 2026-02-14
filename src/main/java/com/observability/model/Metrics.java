package com.observability.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "metrics")
public class Metrics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String targetName;
    
    @Column(nullable = false)
    private String host;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(columnDefinition = "TEXT")
    private String tags;
    
    // CPU Metrics
    @Column(name = "cpu_usage_percent")
    private Double cpuUsagePercent;
    
    @Column(name = "cpu_load_1min")
    private Double cpuLoad1Min;
    
    @Column(name = "cpu_load_5min")
    private Double cpuLoad5Min;
    
    @Column(name = "cpu_load_15min")
    private Double cpuLoad15Min;
    
    @Column(name = "cpu_cores")
    private Integer cpuCores;
    
    // Memory Metrics
    @Column(name = "memory_total_mb")
    private Long memoryTotalMb;
    
    @Column(name = "memory_used_mb")
    private Long memoryUsedMb;
    
    @Column(name = "memory_free_mb")
    private Long memoryFreeMb;
    
    @Column(name = "memory_usage_percent")
    private Double memoryUsagePercent;
    
    @Column(name = "swap_total_mb")
    private Long swapTotalMb;
    
    @Column(name = "swap_used_mb")
    private Long swapUsedMb;
    
    @Column(name = "swap_free_mb")
    private Long swapFreeMb;
    
    @Column(name = "swap_usage_percent")
    private Double swapUsagePercent;
    
    // System Info
    @Column(name = "hostname")
    private String hostname;
    
    @Column(name = "uptime")
    private String uptime;
    
    @Column(name = "os_info")
    private String osInfo;
    
    // Constructors
    public Metrics() {}
    
    public Metrics(String targetName, String host) {
        this.targetName = targetName;
        this.host = host;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }
    
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    
    public Double getCpuUsagePercent() { return cpuUsagePercent; }
    public void setCpuUsagePercent(Double cpuUsagePercent) { this.cpuUsagePercent = cpuUsagePercent; }
    
    public Double getCpuLoad1Min() { return cpuLoad1Min; }
    public void setCpuLoad1Min(Double cpuLoad1Min) { this.cpuLoad1Min = cpuLoad1Min; }
    
    public Double getCpuLoad5Min() { return cpuLoad5Min; }
    public void setCpuLoad5Min(Double cpuLoad5Min) { this.cpuLoad5Min = cpuLoad5Min; }
    
    public Double getCpuLoad15Min() { return cpuLoad15Min; }
    public void setCpuLoad15Min(Double cpuLoad15Min) { this.cpuLoad15Min = cpuLoad15Min; }
    
    public Integer getCpuCores() { return cpuCores; }
    public void setCpuCores(Integer cpuCores) { this.cpuCores = cpuCores; }
    
    public Long getMemoryTotalMb() { return memoryTotalMb; }
    public void setMemoryTotalMb(Long memoryTotalMb) { this.memoryTotalMb = memoryTotalMb; }
    
    public Long getMemoryUsedMb() { return memoryUsedMb; }
    public void setMemoryUsedMb(Long memoryUsedMb) { this.memoryUsedMb = memoryUsedMb; }
    
    public Long getMemoryFreeMb() { return memoryFreeMb; }
    public void setMemoryFreeMb(Long memoryFreeMb) { this.memoryFreeMb = memoryFreeMb; }
    
    public Double getMemoryUsagePercent() { return memoryUsagePercent; }
    public void setMemoryUsagePercent(Double memoryUsagePercent) { this.memoryUsagePercent = memoryUsagePercent; }
    
    public Long getSwapTotalMb() { return swapTotalMb; }
    public void setSwapTotalMb(Long swapTotalMb) { this.swapTotalMb = swapTotalMb; }
    
    public Long getSwapUsedMb() { return swapUsedMb; }
    public void setSwapUsedMb(Long swapUsedMb) { this.swapUsedMb = swapUsedMb; }
    
    public Long getSwapFreeMb() { return swapFreeMb; }
    public void setSwapFreeMb(Long swapFreeMb) { this.swapFreeMb = swapFreeMb; }
    
    public Double getSwapUsagePercent() { return swapUsagePercent; }
    public void setSwapUsagePercent(Double swapUsagePercent) { this.swapUsagePercent = swapUsagePercent; }
    
    public String getHostname() { return hostname; }
    public void setHostname(String hostname) { this.hostname = hostname; }
    
    public String getUptime() { return uptime; }
    public void setUptime(String uptime) { this.uptime = uptime; }
    
    public String getOsInfo() { return osInfo; }
    public void setOsInfo(String osInfo) { this.osInfo = osInfo; }
}
