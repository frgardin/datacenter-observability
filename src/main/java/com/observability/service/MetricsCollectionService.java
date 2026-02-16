package com.observability.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.observability.config.ObservabilityConfig;
import com.observability.dto.MetricsDTO;

@Service
public class MetricsCollectionService {

    private static final Logger logger = LoggerFactory.getLogger(MetricsCollectionService.class);

    @Autowired
    private ObservabilityConfig config;

    @Autowired
    private HistoricalMetricsService historicalMetricsService;

    public MetricsDTO collectMetrics(ObservabilityConfig.TargetConfig target) {
        try {
            MetricsDTO metrics;
            if ("localhost".equals(target.getHost()) || "127.0.0.1".equals(target.getHost())) {
                metrics = collectLocalMetrics(target);
            } else {
                metrics = collectRemoteMetrics(target);
            }

            // Save metrics to database for historical data
            if (metrics != null) {
                historicalMetricsService.saveMetrics(metrics);
            }

            return metrics;
        } catch (Exception e) {
            logger.error("Failed to collect metrics from {}: {}", target.getName(), e.getMessage());
            return null;
        }
    }

    private MetricsDTO collectLocalMetrics(ObservabilityConfig.TargetConfig target) {
        try {
            MetricsDTO metrics = new MetricsDTO();
            metrics.setTarget(target.getName());
            metrics.setHost(target.getHost());
            metrics.setTimestamp(LocalDateTime.now());
            metrics.setTags(target.getTags());

            // CPU Metrics using system commands
            MetricsDTO.CpuMetrics cpuMetrics = new MetricsDTO.CpuMetrics();

            // Get CPU cores
            try {
                Process process = Runtime.getRuntime().exec("nproc");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String coresStr = reader.readLine();
                if (coresStr != null) {
                    cpuMetrics.setCores(Integer.parseInt(coresStr.trim()));
                }
                process.waitFor();
            } catch (Exception e) {
                cpuMetrics.setCores(Runtime.getRuntime().availableProcessors());
            }

            // Get CPU usage (simplified)
            cpuMetrics.setUsagePercent(getCpuUsage());

            // Get load averages
            try {
                Process process = Runtime.getRuntime().exec("cat /proc/loadavg");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String loadAvg = reader.readLine();
                if (loadAvg != null) {
                    String[] loads = loadAvg.split("\\s+");
                    if (loads.length >= 3) {
                        cpuMetrics.setLoad1Min(Double.parseDouble(loads[0]));
                        cpuMetrics.setLoad5Min(Double.parseDouble(loads[1]));
                        cpuMetrics.setLoad15Min(Double.parseDouble(loads[2]));
                    }
                }
                process.waitFor();
            } catch (Exception e) {
                logger.warn("Could not get load averages: {}", e.getMessage());
            }

            metrics.setCpu(cpuMetrics);

            // Memory Metrics
            MetricsDTO.MemoryMetrics memoryMetrics = new MetricsDTO.MemoryMetrics();
            try {
                Process process = Runtime.getRuntime().exec("free -m");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Mem:")) {
                        String[] parts = line.split("\\s+");
                        if (parts.length >= 4) {
                            memoryMetrics.setTotalMb(Long.parseLong(parts[1]));
                            memoryMetrics.setUsedMb(Long.parseLong(parts[2]));
                            memoryMetrics.setFreeMb(Long.parseLong(parts[3]));
                            memoryMetrics.setUsagePercent(
                                    (double) memoryMetrics.getUsedMb() / memoryMetrics.getTotalMb() * 100);
                        }
                    } else if (line.startsWith("Swap:")) {
                        String[] parts = line.split("\\s+");
                        if (parts.length >= 4) {
                            memoryMetrics.setSwapTotalMb(Long.parseLong(parts[1]));
                            memoryMetrics.setSwapUsedMb(Long.parseLong(parts[2]));
                            memoryMetrics.setSwapFreeMb(Long.parseLong(parts[3]));
                            if (memoryMetrics.getSwapTotalMb() > 0) {
                                memoryMetrics.setSwapUsagePercent(
                                        (double) memoryMetrics.getSwapUsedMb() / memoryMetrics.getSwapTotalMb() * 100);
                            }
                        }
                    }
                }
                process.waitFor();
            } catch (Exception e) {
                logger.warn("Could not get memory info: {}", e.getMessage());
            }
            metrics.setMemory(memoryMetrics);

            // Disk Metrics
            metrics.setDisk(collectLocalDiskMetrics());

            // System Info
            MetricsDTO.SystemInfo systemInfo = new MetricsDTO.SystemInfo();
            try {
                // Get hostname
                Process process = Runtime.getRuntime().exec("hostname");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                systemInfo.setHostname(reader.readLine());
                process.waitFor();

                // Get uptime
                process = Runtime.getRuntime().exec("uptime -p");
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                systemInfo.setUptime(reader.readLine());
                process.waitFor();

                // Get OS info
                process = Runtime.getRuntime().exec("cat /etc/os-release | grep PRETTY_NAME");
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String osLine = reader.readLine();
                if (osLine != null) {
                    systemInfo.setOsInfo(osLine.split("=")[1].replace("\"", ""));
                }
                process.waitFor();
            } catch (Exception e) {
                logger.warn("Could not get system info: {}", e.getMessage());
                systemInfo.setHostname("localhost");
                systemInfo.setUptime("Unknown");
                systemInfo.setOsInfo("Unknown OS");
            }
            metrics.setSystem(systemInfo);

            return metrics;

        } catch (Exception e) {
            logger.error("Failed to collect local metrics: {}", e.getMessage());
            return null;
        }
    }

    private List<MetricsDTO.DiskMetricsDTO> collectLocalDiskMetrics() {
        List<MetricsDTO.DiskMetricsDTO> diskMetrics = new ArrayList<>();

        try {
            Process process = Runtime.getRuntime().exec("df -h");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            // Skip header line
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 6) {
                    MetricsDTO.DiskMetricsDTO diskMetric = new MetricsDTO.DiskMetricsDTO();
                    diskMetric.setFilesystem(parts[0]);
                    diskMetric.setSize(parts[1]);
                    diskMetric.setUsed(parts[2]);
                    diskMetric.setAvailable(parts[3]);
                    diskMetric.setUsagePercent(Integer.parseInt(parts[4].replace("%", "")));
                    diskMetric.setMountPoint(parts[5]);
                    diskMetrics.add(diskMetric);
                }
            }
            process.waitFor();
        } catch (Exception e) {
            logger.error("Failed to collect disk metrics: {}", e.getMessage());
        }

        return diskMetrics;
    }

    private MetricsDTO collectRemoteMetrics(ObservabilityConfig.TargetConfig target) {
        Session session = null;
        ChannelExec channel = null;

        try {
            JSch jsch = new JSch();

            // Setup SSH connection
            if (target.getSshKey() != null && !target.getSshKey().isEmpty()) {
                jsch.addIdentity(target.getSshKey());
            }

            session = jsch.getSession(target.getUsername(), target.getHost(), target.getPort());

            if (target.getPassword() != null && !target.getPassword().isEmpty()) {
                session.setPassword(target.getPassword());
            }

            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(config.getCollection().getTimeout() * 1000);
            session.connect();

            MetricsDTO metrics = new MetricsDTO();
            metrics.setTarget(target.getName());
            metrics.setHost(target.getHost());
            metrics.setTimestamp(LocalDateTime.now());
            metrics.setTags(target.getTags());

            // Collect metrics
            metrics.setCpu(collectRemoteCpuMetrics(session));
            metrics.setMemory(collectRemoteMemoryMetrics(session));
            metrics.setDisk(collectRemoteDiskMetrics(session));
            metrics.setSystem(collectRemoteSystemInfo(session));

            return metrics;

        } catch (Exception e) {
            logger.error("Failed to collect remote metrics from {}: {}", target.getName(), e.getMessage());
            return null;
        } finally {
            if (channel != null)
                channel.disconnect();
            if (session != null)
                session.disconnect();
        }
    }

    private MetricsDTO.CpuMetrics collectRemoteCpuMetrics(Session session) throws Exception {
        MetricsDTO.CpuMetrics cpuMetrics = new MetricsDTO.CpuMetrics();

        // Get CPU usage
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand("top -bn1 | grep 'Cpu(s)' | awk '{print $2}' | sed 's/%us,//'");
        channel.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
        String cpuUsageStr = reader.readLine();
        channel.disconnect();

        if (cpuUsageStr != null && !cpuUsageStr.isEmpty()) {
            cpuMetrics.setUsagePercent(Double.parseDouble(cpuUsageStr.trim()));
        }

        // Get load averages and cores
        channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand("cat /proc/loadavg && nproc");
        channel.connect();

        reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
        String loadAvg = reader.readLine();
        String coresStr = reader.readLine();
        channel.disconnect();

        if (loadAvg != null) {
            String[] loads = loadAvg.split("\\s+");
            if (loads.length >= 3) {
                cpuMetrics.setLoad1Min(Double.parseDouble(loads[0]));
                cpuMetrics.setLoad5Min(Double.parseDouble(loads[1]));
                cpuMetrics.setLoad15Min(Double.parseDouble(loads[2]));
            }
        }

        if (coresStr != null) {
            cpuMetrics.setCores(Integer.parseInt(coresStr.trim()));
        }

        return cpuMetrics;
    }

    private MetricsDTO.MemoryMetrics collectRemoteMemoryMetrics(Session session) throws Exception {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand("free -m");
        channel.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
        String line;
        MetricsDTO.MemoryMetrics memoryMetrics = new MetricsDTO.MemoryMetrics();

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Mem:")) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 4) {
                    memoryMetrics.setTotalMb(Long.parseLong(parts[1]));
                    memoryMetrics.setUsedMb(Long.parseLong(parts[2]));
                    memoryMetrics.setFreeMb(Long.parseLong(parts[3]));
                    memoryMetrics
                            .setUsagePercent((double) memoryMetrics.getUsedMb() / memoryMetrics.getTotalMb() * 100);
                }
            } else if (line.startsWith("Swap:")) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 4) {
                    memoryMetrics.setSwapTotalMb(Long.parseLong(parts[1]));
                    memoryMetrics.setSwapUsedMb(Long.parseLong(parts[2]));
                    memoryMetrics.setSwapFreeMb(Long.parseLong(parts[3]));
                    if (memoryMetrics.getSwapTotalMb() > 0) {
                        memoryMetrics.setSwapUsagePercent(
                                (double) memoryMetrics.getSwapUsedMb() / memoryMetrics.getSwapTotalMb() * 100);
                    }
                }
            }
        }

        channel.disconnect();
        return memoryMetrics;
    }

    private List<MetricsDTO.DiskMetricsDTO> collectRemoteDiskMetrics(Session session) throws Exception {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand("df -h");
        channel.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
        String line;
        List<MetricsDTO.DiskMetricsDTO> diskMetrics = new ArrayList<>();

        // Skip header line
        reader.readLine();

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\s+");
            if (parts.length >= 6) {
                MetricsDTO.DiskMetricsDTO diskMetric = new MetricsDTO.DiskMetricsDTO();
                diskMetric.setFilesystem(parts[0]);
                diskMetric.setSize(parts[1]);
                diskMetric.setUsed(parts[2]);
                diskMetric.setAvailable(parts[3]);
                diskMetric.setUsagePercent(Integer.parseInt(parts[4].replace("%", "")));
                diskMetric.setMountPoint(parts[5]);
                diskMetrics.add(diskMetric);
            }
        }

        channel.disconnect();
        return diskMetrics;
    }

    private MetricsDTO.SystemInfo collectRemoteSystemInfo(Session session) throws Exception {
        MetricsDTO.SystemInfo systemInfo = new MetricsDTO.SystemInfo();

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand("hostname && uptime -p && grep PRETTY_NAME /etc/os-release");
        channel.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
        systemInfo.setHostname(reader.readLine());
        systemInfo.setUptime(reader.readLine());
        String osLine = reader.readLine();
        if (osLine != null) {
            systemInfo.setOsInfo(osLine.split("=")[1].replace("\"", ""));
        }
        channel.disconnect();

        return systemInfo;
    }

    private double getCpuUsage() {
        try {
            // Simple CPU usage calculation using /proc/stat
            long startIdle = 0, startTotal = 0;

            Process process = Runtime.getRuntime().exec("cat /proc/stat");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if (line != null && line.startsWith("cpu ")) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 5) {
                    long user = Long.parseLong(parts[1]);
                    long nice = Long.parseLong(parts[2]);
                    long system = Long.parseLong(parts[3]);
                    long idle = Long.parseLong(parts[4]);

                    startIdle = idle;
                    startTotal = user + nice + system + idle;
                }
            }
            process.waitFor();

            Thread.sleep(1000); // Wait 1 second

            long endIdle = 0, endTotal = 0;
            process = Runtime.getRuntime().exec("cat /proc/stat");
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            line = reader.readLine();
            if (line != null && line.startsWith("cpu ")) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 5) {
                    long user = Long.parseLong(parts[1]);
                    long nice = Long.parseLong(parts[2]);
                    long system = Long.parseLong(parts[3]);
                    long idle = Long.parseLong(parts[4]);

                    endIdle = idle;
                    endTotal = user + nice + system + idle;
                }
            }
            process.waitFor();

            long diffIdle = endIdle - startIdle;
            long diffTotal = endTotal - startTotal;

            if (diffTotal > 0) {
                return (1.0 - (double) diffIdle / diffTotal) * 100;
            }
        } catch (Exception e) {
            logger.warn("Could not calculate CPU usage: {}", e.getMessage());
        }

        return 0.0;
    }
}
