package com.sentinela.incident.repository;

import com.sentinela.incident.entity.IncidentNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentNoteRepository extends JpaRepository<IncidentNote, Long> {
    List<IncidentNote> findByIncidentId(Long incidentId);
}
