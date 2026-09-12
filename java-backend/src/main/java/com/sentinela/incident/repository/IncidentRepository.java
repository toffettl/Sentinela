package com.sentinela.incident.repository;

import com.sentinela.incident.entity.Incident;
import com.sentinela.incident.entity.IncidentSeverity;
import com.sentinela.incident.entity.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    long countBySeverity(IncidentSeverity severity);
    long countByStatus(IncidentStatus status);
}
