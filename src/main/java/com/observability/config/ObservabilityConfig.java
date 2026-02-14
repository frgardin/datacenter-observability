package com.observability.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "observability")
@EnableScheduling
@EnableWebSocketMessageBroker
public class ObservabilityConfig implements WebSocketMessageBrokerConfigurer {

    private List<TargetConfig> targets;
    private CollectionConfig collection;
    private AlertsConfig alerts;

    public static class TargetConfig {
        private String name;
        private String host;
        private int port = 22;
        private String username;
        private String password;
        private String sshKey;
        private List<String> tags;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getSshKey() { return sshKey; }
        public void setSshKey(String sshKey) { this.sshKey = sshKey; }
        
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
    }

    public static class CollectionConfig {
        private int interval = 30;
        private int timeout = 10;

        public int getInterval() { return interval; }
        public void setInterval(int interval) { this.interval = interval; }
        
        public int getTimeout() { return timeout; }
        public void setTimeout(int timeout) { this.timeout = timeout; }
    }

    public static class AlertsConfig {
        private ThresholdConfig cpu;
        private ThresholdConfig ram;
        private ThresholdConfig disk;

        public static class ThresholdConfig {
            private int warning = 70;
            private int critical = 90;

            public int getWarning() { return warning; }
            public void setWarning(int warning) { this.warning = warning; }
            
            public int getCritical() { return critical; }
            public void setCritical(int critical) { this.critical = critical; }
        }

        public ThresholdConfig getCpu() { return cpu; }
        public void setCpu(ThresholdConfig cpu) { this.cpu = cpu; }
        
        public ThresholdConfig getRam() { return ram; }
        public void setRam(ThresholdConfig ram) { this.ram = ram; }
        
        public ThresholdConfig getDisk() { return disk; }
        public void setDisk(ThresholdConfig disk) { this.disk = disk; }
    }

    // Getters and Setters
    public List<TargetConfig> getTargets() { return targets; }
    public void setTargets(List<TargetConfig> targets) { this.targets = targets; }
    
    public CollectionConfig getCollection() { return collection; }
    public void setCollection(CollectionConfig collection) { this.collection = collection; }
    
    public AlertsConfig getAlerts() { return alerts; }
    public void setAlerts(AlertsConfig alerts) { this.alerts = alerts; }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }
}
