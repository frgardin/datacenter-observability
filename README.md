# Datacenter Observability - Java 25 + Spring Boot

A modern Java-based system for monitoring CPU, RAM, and disk usage across multiple machines in a datacenter. Built with Java 25, Spring Boot, and Maven.

## Features

- **Real-time Monitoring**: Track CPU utilization, RAM usage, and disk consumption on multiple machines
- **Modern Web Dashboard**: Responsive UI built with Tailwind CSS and JavaScript
- **SSH-based Collection**: Secure metrics collection without installing agents on target machines
- **Spring Boot REST API**: Complete RESTful API for metrics access
- **Alerting System**: Configurable thresholds for CPU, RAM, and disk usage
- **Java 25**: Latest Java features and performance improvements
- **Maven Build**: Standard Maven project structure and dependency management

## Architecture

```
┌─────────────────┐         ┌─────────────────┐
│  Observer       │         │  Target Machine │
│  Machine        │◄─────── │  (SSH Access)   │
│  - Spring Boot  │  SSH    │  - CPU Metrics  │
│  - REST API     │         │  - RAM Metrics  │
│  - Web UI       │         │  - Disk Metrics │
│  - H2 Database │         │                 │
└─────────────────┘         └─────────────────┘
         │                          │
         │                          │
         └────── Multiple Targets ──┘
```

## Prerequisites

- **Java 25** or higher
- **Maven 3.8** or higher
- **SSH access** to target machines (for remote monitoring)

## Quick Start

### 1. Clone and Build

```bash
git clone <repository-url>
cd datacenter-observability
mvn clean install
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 3. Access the Dashboard

Open your browser and navigate to:
- **Dashboard**: `http://localhost:8080`
- **API Endpoints**: `http://localhost:8080/api/*`
- **H2 Console**: `http://localhost:8080/h2-console`

## Configuration

### Application Configuration

Edit `src/main/resources/application.yml` to configure targets and alerts:

```yaml
observability:
  targets:
    - name: "localhost"
      host: "127.0.0.1"
      port: 22
      username: "your-username"
      password: "your-password"  # or use SSH key
      tags:
        - "local"
        - "test"
  
  collection:
    interval: 30  # seconds
    timeout: 10   # seconds
  
  alerts:
    cpu:
      warning: 70
      critical: 90
    ram:
      warning: 80
      critical: 95
    disk:
      warning: 80
      critical: 90
```

### SSH Authentication

The application supports both password and SSH key authentication:

```yaml
targets:
  - name: "server-01"
    host: "192.168.1.100"
    username: "monitor"
    password: "your-password"  # Password authentication
    
  - name: "server-02"
    host: "192.168.1.101"
    username: "monitor"
    ssh_key: "/home/user/.ssh/id_rsa"  # SSH key authentication
```

## API Endpoints

### GET /api/metrics
Retrieve current metrics for all machines.

```bash
curl http://localhost:8080/api/metrics
```

### GET /api/metrics/{target}
Retrieve current metrics for a specific target.

```bash
curl http://localhost:8080/api/metrics/localhost
```

### GET /api/targets
List all configured target machines with their status.

```bash
curl http://localhost:8080/api/targets
```

### GET /api/alerts
Get current active alerts based on configured thresholds.

```bash
curl http://localhost:8080/api/alerts
```

## Dashboard Features

The web dashboard provides:

- **Real-time Updates**: Auto-refreshes every 30 seconds
- **System Overview**: Total targets, online/offline status
- **Target Cards**: Individual metrics for each monitored machine
- **Visual Indicators**: Progress bars for CPU, memory, and disk usage
- **Alert Display**: Active warnings and critical alerts
- **Responsive Design**: Works on desktop and mobile devices

## Project Structure

```
datacenter-observability/
├── pom.xml                           # Maven configuration
├── src/
│   ├── main/
│   │   ├── java/com/observability/
│   │   │   ├── DatacenterObservabilityApplication.java  # Main application
│   │   │   ├── config/
│   │   │   │   └── ObservabilityConfig.java            # Configuration
│   │   │   ├── controller/
│   │   │   │   └── MetricsController.java              # REST API
│   │   │   ├── service/
│   │   │   │   └── MetricsCollectionService.java       # Metrics collection
│   │   │   ├── dto/
│   │   │   │   └── MetricsDTO.java                    # Data transfer objects
│   │   │   └── model/
│   │   │       ├── Metrics.java                        # JPA entities
│   │   │       └── DiskMetrics.java
│   │   └── resources/
│   │       ├── application.yml                          # Application config
│   │       ├── static/
│   │       │   └── index.html                         # Web dashboard
│   │       └── templates/
│   └── test/
└── README.md
```

## Technology Stack

- **Java 25**: Latest Java version with modern features
- **Spring Boot 3.2**: Framework for REST API and application management
- **Maven**: Build and dependency management
- **H2 Database**: In-memory database for metrics storage
- **JSch**: SSH library for remote connections
- **Tailwind CSS**: Modern CSS framework for UI
- **JavaScript**: Client-side dashboard functionality

## Development

### Running Tests

```bash
mvn test
```

### Building for Production

```bash
mvn clean package
java -jar target/datacenter-observability-1.0.0.jar
```

### Adding New Metrics

1. Update `MetricsDTO.java` to include new metric types
2. Modify `MetricsCollectionService.java` to collect new metrics
3. Update the dashboard HTML to display new metrics

## Security Considerations

- SSH connections use secure authentication
- Support for SSH key authentication (recommended)
- No agents required on target machines
- Configure firewall rules to allow SSH from observer machine only

## Troubleshooting

### Common Issues

1. **SSH Connection Failed**
   - Verify SSH credentials in `application.yml`
   - Check network connectivity to target machines
   - Ensure SSH service is running on targets

2. **Permission Denied**
   - Verify user has necessary permissions on target machines
   - For SSH keys, ensure proper file permissions (600)

3. **Metrics Not Updating**
   - Check application logs for errors
   - Verify collection interval in configuration
   - Ensure target machines are accessible

### Logging

Application logs are displayed in the console and can be configured in `application.yml`.

## License

This project is licensed under the MIT License.