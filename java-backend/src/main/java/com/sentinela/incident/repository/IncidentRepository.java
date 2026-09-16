package com.sentinela.incident.repository;

import com.sentinela.incident.entity.Incident;
import com.sentinela.incident.entity.IncidentSeverity;
import com.sentinela.incident.entity.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    long countBySeverity(IncidentSeverity severity);
    long countByStatus(IncidentStatus status);
    List<Incident> findByStatus(IncidentStatus status);
    List<Incident> findBySeverity(IncidentSeverity severity);
    List<Incident> findByStatusAndSeverity(IncidentStatus status, IncidentSeverity severity);
}
