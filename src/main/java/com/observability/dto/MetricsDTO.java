package com.observability.dto;

import java.time.LocalDateTime;
import java.util.List;

public class MetricsDTO {
    
    private String target;
    private String host;
    private LocalDateTime timestamp;
    private List<String> tags;
    private CpuMetrics cpu;
    private MemoryMetrics memory;
    private List<DiskMetricsDTO> disk;
    private SystemInfo system;

    public static class CpuMetrics {
        private Double usagePercent;
        private Double load1Min;
        private Double load5Min;
        private Double load15Min;
        private Integer cores;

        // Getters and Setters
        public Double getUsagePercent() { return usagePercent; }
        public void setUsagePercent(Double usagePercent) { this.usagePercent = usagePercent; }
        
        public Double getLoad1Min() { return load1Min; }
        public void setLoad1Min(Double load1Min) { this.load1Min = load1Min; }
        
        public Double getLoad5Min() { return load5Min; }
        public void setLoad5Min(Double load5Min) { this.load5Min = load5Min; }
        
        public Double getLoad15Min() { return load15Min; }
        public void setLoad15Min(Double load15Min) { this.load15Min = load15Min; }
        
        public Integer getCores() { return cores; }
        public void setCores(Integer cores) { this.cores = cores; }
    }

    public static class MemoryMetrics {
        private Long totalMb;
        private Long usedMb;
        private Long freeMb;
        private Double usagePercent;
        private Long swapTotalMb;
        private Long swapUsedMb;
        private Long swapFreeMb;
        private Double swapUsagePercent;

        // Getters and Setters
        public Long getTotalMb() { return totalMb; }
        public void setTotalMb(Long totalMb) { this.totalMb = totalMb; }
        
        public Long getUsedMb() { return usedMb; }
        public void setUsedMb(Long usedMb) { this.usedMb = usedMb; }
        
        public Long getFreeMb() { return freeMb; }
        public void setFreeMb(Long freeMb) { this.freeMb = freeMb; }
        
        public Double getUsagePercent() { return usagePercent; }
        public void setUsagePercent(Double usagePercent) { this.usagePercent = usagePercent; }
        
        public Long getSwapTotalMb() { return swapTotalMb; }
        public void setSwapTotalMb(Long swapTotalMb) { this.swapTotalMb = swapTotalMb; }
        
        public Long getSwapUsedMb() { return swapUsedMb; }
        public void setSwapUsedMb(Long swapUsedMb) { this.swapUsedMb = swapUsedMb; }
        
        public Long getSwapFreeMb() { return swapFreeMb; }
        public void setSwapFreeMb(Long swapFreeMb) { this.swapFreeMb = swapFreeMb; }
        
        public Double getSwapUsagePercent() { return swapUsagePercent; }
        public void setSwapUsagePercent(Double swapUsagePercent) { this.swapUsagePercent = swapUsagePercent; }
    }

    public static class DiskMetricsDTO {
        private String filesystem;
        private String size;
        private String used;
        private String available;
        private Integer usagePercent;
        private String mountPoint;

        // Getters and Setters
        public String getFilesystem() { return filesystem; }
        public void setFilesystem(String filesystem) { this.filesystem = filesystem; }
        
        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }
        
        public String getUsed() { return used; }
        public void setUsed(String used) { this.used = used; }
        
        public String getAvailable() { return available; }
        public void setAvailable(String available) { this.available = available; }
        
        public Integer getUsagePercent() { return usagePercent; }
        public void setUsagePercent(Integer usagePercent) { this.usagePercent = usagePercent; }
        
        public String getMountPoint() { return mountPoint; }
        public void setMountPoint(String mountPoint) { this.mountPoint = mountPoint; }
    }

    public static class SystemInfo {
        private String hostname;
        private String uptime;
        private String osInfo;

        // Getters and Setters
        public String getHostname() { return hostname; }
        public void setHostname(String hostname) { this.hostname = hostname; }
        
        public String getUptime() { return uptime; }
        public void setUptime(String uptime) { this.uptime = uptime; }
        
        public String getOsInfo() { return osInfo; }
        public void setOsInfo(String osInfo) { this.osInfo = osInfo; }
    }

    // Main class getters and setters
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public CpuMetrics getCpu() { return cpu; }
    public void setCpu(CpuMetrics cpu) { this.cpu = cpu; }
    
    public MemoryMetrics getMemory() { return memory; }
    public void setMemory(MemoryMetrics memory) { this.memory = memory; }
    
    public List<DiskMetricsDTO> getDisk() { return disk; }
    public void setDisk(List<DiskMetricsDTO> disk) { this.disk = disk; }
    
    public SystemInfo getSystem() { return system; }
    public void setSystem(SystemInfo system) { this.system = system; }
}
