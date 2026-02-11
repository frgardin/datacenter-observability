# Datacenter Observability

A lightweight system for monitoring CPU, RAM, and disk usage across multiple machines in a datacenter. One central observer machine collects and aggregates metrics from target machines in real-time.

## Features

- **Real-time Monitoring**: Track CPU utilization, RAM usage, and disk consumption on multiple machines
- **Centralized Dashboard**: View all metrics from a single observer machine
- **Configurable Targets**: Easily add or remove machines from the monitoring pool
- **Alerting System**: Get notified when metrics exceed defined thresholds
- **Historical Data**: Store and query historical metrics for analysis
- **Lightweight Agents**: Minimal resource footprint on monitored machines

## Architecture

```
┌─────────────────┐         ┌─────────────────┐
│  Observer       │         │  Target Machine │
│  Machine        │◄───────│  (Agent)        │
│  (Dashboard/API)│  HTTPS  │  - CPU Monitor  │
│  - Collector    │         │  - RAM Monitor  │
│  - Database     │         │  - Disk Monitor │
│  - Web UI       │         │                 │
└─────────────────┘         └─────────────────┘
         │                          │
         │                          │
         └────── Multiple Targets ──┘
```

## Prerequisites

- **Observer Machine**: Python 3.8+ or Node.js 18+
- **Target Machines**: Python 3.8+ (agent script) or SSH access
- **Network**: HTTPS/SSH connectivity between observer and targets

## Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/your-org/datacenter-observability.git
cd datacenter-observability
```

### 2. Install Dependencies

**Option A: Python (Recommended)**
```bash
pip install -r requirements.txt
```

**Option B: Node.js**
```bash
npm install
```

### 3. Configure Target Machines

Edit `config/targets.yaml` to define machines to monitor:

```yaml
targets:
  - host: "server-01.local"
    port: 22
    username: "monitor"
    password: "your-password"  # or use SSH key
    tags:
      - "web-server"
      - "production"
      
  - host: "server-02.local"
    port: 22
    username: "monitor"
    ssh_key: "/home/user/.ssh/id_rsa"
    tags:
      - "database"
      - "production"
```

### 4. Run the Observer

```bash
python main.py
# or
npm start
```

The dashboard will be available at `http://localhost:3000`

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `OBSERVER_PORT` | Web server port | 3000 |
| `OBSERVER_HOST` | Web server host | 0.0.0.0 |
| `METRICS_INTERVAL` | Collection interval (seconds) | 30 |
| `DATA_RETENTION` | Data retention period (days) | 30 |
| `LOG_LEVEL` | Logging level | info |

### Alert Thresholds

Configure alerting in `config/alerts.yaml`:

```yaml
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

## API Endpoints

### GET /api/metrics
Retrieve current metrics for all machines.

```bash
curl http://localhost:3000/api/metrics
```

### GET /api/machines
List all configured target machines.

```bash
curl http://localhost:3000/api/machines
```

### GET /api/history/:hostname
Get historical metrics for a specific machine.

```bash
curl http://localhost:3000/api/history/server-01.local?period=24h
```

## Project Structure

```
datacenter-observability/
├── config/
│   ├── targets.yaml       # Target machine definitions
│   └── alerts.yaml        # Alert thresholds
├── src/
│   ├── collector/         # Metrics collection logic
│   ├── api/               # REST API endpoints
│   ├── agents/            # Target machine agents
│   ├── models/            # Data models
│   └── utils/             # Utility functions
├── tests/                 # Unit and integration tests
├── main.py               # Application entry point
├── requirements.txt      # Python dependencies
└── README.md            # This file
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For issues and feature requests, please open a GitHub issue.
