package com.observability.model;

import jakarta.persistence.*;

@Entity
@Table(name = "disk_metrics")
public class DiskMetrics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "metrics_id", nullable = false)
    private Metrics metrics;
    
    @Column(nullable = false)
    private String filesystem;
    
    @Column(nullable = false)
    private String size;
    
    @Column(nullable = false)
    private String used;
    
    @Column(nullable = false)
    private String available;
    
    @Column(name = "usage_percent", nullable = false)
    private Integer usagePercent;
    
    @Column(name = "mount_point", nullable = false)
    private String mountPoint;
    
    // Constructors
    public DiskMetrics() {}
    
    public DiskMetrics(Metrics metrics, String filesystem, String size, String used, 
                      String available, Integer usagePercent, String mountPoint) {
        this.metrics = metrics;
        this.filesystem = filesystem;
        this.size = size;
        this.used = used;
        this.available = available;
        this.usagePercent = usagePercent;
        this.mountPoint = mountPoint;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Metrics getMetrics() { return metrics; }
    public void setMetrics(Metrics metrics) { this.metrics = metrics; }
    
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
