package com.sentinela.detection.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "detections")
@Getter
@Setter
@NoArgsConstructor
public class Detection {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50) private String pattern;
    @Column(nullable = false, length = 45) private String ip;
    @Column(name = "event_count", nullable = false) private int eventCount;
    @Column(name = "event_ids", nullable = false, columnDefinition = "TEXT") private String eventIds;
    @Column(name = "risk_points", nullable = false) private int riskPoints;

    public void setPattern(String pattern) { this.pattern = pattern; }
    public void setIp(String ip) { this.ip = ip; }
    public void setEventCount(int eventCount) { this.eventCount = eventCount; }
    public void setEventIds(String eventIds) { this.eventIds = eventIds; }
    public void setRiskPoints(int riskPoints) { this.riskPoints = riskPoints; }
}
