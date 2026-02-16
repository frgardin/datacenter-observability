package com.observability.repository;

import com.observability.model.Metrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MetricsRepository extends JpaRepository<Metrics, Long> {
    
    @Query("SELECT m FROM Metrics m WHERE m.targetName = :targetName AND m.timestamp >= :startTime ORDER BY m.timestamp ASC")
    List<Metrics> findByTargetNameAndTimestampAfterOrderByTimestampAsc(@Param("targetName") String targetName, @Param("startTime") LocalDateTime startTime);
    
    @Query("SELECT m FROM Metrics m WHERE m.targetName = :targetName AND m.timestamp BETWEEN :startTime AND :endTime ORDER BY m.timestamp ASC")
    List<Metrics> findByTargetNameAndTimestampBetweenOrderByTimestampAsc(@Param("targetName") String targetName, 
                                                                         @Param("startTime") LocalDateTime startTime, 
                                                                         @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT m FROM Metrics m WHERE m.timestamp >= :startTime ORDER BY m.timestamp DESC")
    List<Metrics> findRecentMetrics(@Param("startTime") LocalDateTime startTime);
    
    @Query("SELECT DISTINCT m.targetName FROM Metrics m")
    List<String> findAllTargetNames();
}
