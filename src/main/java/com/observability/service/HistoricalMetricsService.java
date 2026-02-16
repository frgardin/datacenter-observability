package com.observability.service;

import com.observability.dto.MetricsDTO;
import com.observability.model.Metrics;
import com.observability.repository.MetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoricalMetricsService {

    @Autowired
    private MetricsRepository metricsRepository;

    public List<MetricsDTO> getHistoricalMetrics(String targetName, LocalDateTime startTime, LocalDateTime endTime) {
        List<Metrics> metrics = metricsRepository.findByTargetNameAndTimestampBetweenOrderByTimestampAsc(targetName, startTime, endTime);
        return metrics.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<MetricsDTO> getRecentMetrics(String targetName, int hoursBack) {
        LocalDateTime startTime = LocalDateTime.now().minusHours(hoursBack);
        List<Metrics> metrics = metricsRepository.findByTargetNameAndTimestampAfterOrderByTimestampAsc(targetName, startTime);
        return metrics.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<MetricsDTO> getAllRecentMetrics(int hoursBack) {
        LocalDateTime startTime = LocalDateTime.now().minusHours(hoursBack);
        List<Metrics> metrics = metricsRepository.findRecentMetrics(startTime);
        return metrics.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public void saveMetrics(MetricsDTO metricsDTO) {
        Metrics metrics = convertToEntity(metricsDTO);
        metricsRepository.save(metrics);
    }

    private MetricsDTO convertToDTO(Metrics metrics) {
        MetricsDTO dto = new MetricsDTO();
        dto.setTarget(metrics.getTargetName());
        dto.setHost(metrics.getHost());
        dto.setTimestamp(metrics.getTimestamp());
        
        // CPU Metrics
        MetricsDTO.CpuMetrics cpuMetrics = new MetricsDTO.CpuMetrics();
        cpuMetrics.setUsagePercent(metrics.getCpuUsagePercent());
        cpuMetrics.setLoad1Min(metrics.getCpuLoad1Min());
        cpuMetrics.setLoad5Min(metrics.getCpuLoad5Min());
        cpuMetrics.setLoad15Min(metrics.getCpuLoad15Min());
        cpuMetrics.setCores(metrics.getCpuCores());
        dto.setCpu(cpuMetrics);

        // Memory Metrics
        MetricsDTO.MemoryMetrics memoryMetrics = new MetricsDTO.MemoryMetrics();
        memoryMetrics.setTotalMb(metrics.getMemoryTotalMb());
        memoryMetrics.setUsedMb(metrics.getMemoryUsedMb());
        memoryMetrics.setFreeMb(metrics.getMemoryFreeMb());
        memoryMetrics.setUsagePercent(metrics.getMemoryUsagePercent());
        memoryMetrics.setSwapTotalMb(metrics.getSwapTotalMb());
        memoryMetrics.setSwapUsedMb(metrics.getSwapUsedMb());
        memoryMetrics.setSwapFreeMb(metrics.getSwapFreeMb());
        memoryMetrics.setSwapUsagePercent(metrics.getSwapUsagePercent());
        dto.setMemory(memoryMetrics);

        // System Info
        MetricsDTO.SystemInfo systemInfo = new MetricsDTO.SystemInfo();
        systemInfo.setHostname(metrics.getHostname());
        systemInfo.setUptime(metrics.getUptime());
        systemInfo.setOsInfo(metrics.getOsInfo());
        dto.setSystem(systemInfo);

        return dto;
    }

    private Metrics convertToEntity(MetricsDTO dto) {
        Metrics metrics = new Metrics();
        metrics.setTargetName(dto.getTarget());
        metrics.setHost(dto.getHost());
        metrics.setTimestamp(dto.getTimestamp());

        // CPU Metrics
        if (dto.getCpu() != null) {
            metrics.setCpuUsagePercent(dto.getCpu().getUsagePercent());
            metrics.setCpuLoad1Min(dto.getCpu().getLoad1Min());
            metrics.setCpuLoad5Min(dto.getCpu().getLoad5Min());
            metrics.setCpuLoad15Min(dto.getCpu().getLoad15Min());
            metrics.setCpuCores(dto.getCpu().getCores());
        }

        // Memory Metrics
        if (dto.getMemory() != null) {
            metrics.setMemoryTotalMb(dto.getMemory().getTotalMb());
            metrics.setMemoryUsedMb(dto.getMemory().getUsedMb());
            metrics.setMemoryFreeMb(dto.getMemory().getFreeMb());
            metrics.setMemoryUsagePercent(dto.getMemory().getUsagePercent());
            metrics.setSwapTotalMb(dto.getMemory().getSwapTotalMb());
            metrics.setSwapUsedMb(dto.getMemory().getSwapUsedMb());
            metrics.setSwapFreeMb(dto.getMemory().getSwapFreeMb());
            metrics.setSwapUsagePercent(dto.getMemory().getSwapUsagePercent());
        }

        // System Info
        if (dto.getSystem() != null) {
            metrics.setHostname(dto.getSystem().getHostname());
            metrics.setUptime(dto.getSystem().getUptime());
            metrics.setOsInfo(dto.getSystem().getOsInfo());
        }

        return metrics;
    }
}
