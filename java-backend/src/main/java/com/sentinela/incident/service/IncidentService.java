package com.sentinela.incident.service;

import com.sentinela.event.entity.Event;
import com.sentinela.event.repository.EventRepository;
import com.sentinela.exception.ResourceNotFoundException;
import com.sentinela.incident.dto.CreateIncidentRequest;
import com.sentinela.incident.dto.EventSummary;
import com.sentinela.incident.dto.IncidentNoteRequest;
import com.sentinela.incident.dto.IncidentNoteResponse;
import com.sentinela.incident.dto.IncidentResponse;
import com.sentinela.incident.dto.IncidentUpdateRequest;
import com.sentinela.incident.entity.Incident;
import com.sentinela.incident.entity.IncidentNote;
import com.sentinela.incident.entity.IncidentSeverity;
import com.sentinela.incident.entity.IncidentStatus;
import com.sentinela.incident.repository.IncidentNoteRepository;
import com.sentinela.incident.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final IncidentNoteRepository noteRepository;
    private final EventRepository eventRepository;

    public IncidentService(IncidentRepository incidentRepository,
                          IncidentNoteRepository noteRepository,
                          EventRepository eventRepository) {
        this.incidentRepository = incidentRepository;
        this.noteRepository = noteRepository;
        this.eventRepository = eventRepository;
    }

    public List<IncidentResponse> findAll(IncidentStatus status, IncidentSeverity severity, String user) {
        List<Incident> incidents = new ArrayList<>();

        if (status != null && severity != null) {
            incidents = incidentRepository.findByStatusAndSeverity(status, severity);
        } else if (status != null) {
            incidents = incidentRepository.findByStatus(status);
        } else if (severity != null) {
            incidents = incidentRepository.findBySeverity(severity);
        } else {
            incidents = incidentRepository.findAll();
        }

        if (user != null && !user.isBlank()) {
            incidents = incidents.stream()
                    .filter(incident -> incident.getUserInvolved() != null &&
                            incident.getUserInvolved().equalsIgnoreCase(user))
                    .toList();
        }

        return incidents.stream()
                .map(IncidentResponse::fromEntity)
                .toList();
    }

    public IncidentResponse findById(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidente não encontrado"));
        return IncidentResponse.fromEntity(incident);
    }

    public List<EventSummary> findEventsByIncidentId(Long incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incidente não encontrado"));

        if (incident.getEvents() == null) {
            return List.of();
        }

        return incident.getEvents().stream()
                .map(EventSummary::fromEntity)
                .toList();
    }

    public IncidentResponse updateStatus(Long id, IncidentUpdateRequest request) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidente não encontrado"));

        validateStatusTransition(incident.getStatus(), request.getStatus());
        incident.setStatus(request.getStatus());

        return IncidentResponse.fromEntity(incidentRepository.save(incident));
    }

    public IncidentNoteResponse addNote(Long incidentId, IncidentNoteRequest request) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incidente não encontrado"));

        IncidentNote note = new IncidentNote();
        note.setIncident(incident);
        note.setContent(request.getContent());

        return IncidentNoteResponse.fromEntity(noteRepository.save(note));
    }

    public IncidentResponse createIncident(CreateIncidentRequest request) {
        Incident incident = new Incident();
        incident.setTitle(request.getTitle());
        incident.setDescription(request.getDescription());
        incident.setRiskScore(request.getRiskScore());
        incident.setUserInvolved(request.getUserInvolved());
        incident.setIpInvolved(request.getIpInvolved());
        incident.setAssetInvolved(request.getAssetInvolved());

        IncidentSeverity severity = calculateSeverity(request.getRiskScore());
        incident.setSeverity(severity);
        incident.setStatus(IncidentStatus.OPEN);

        if (request.getEventIds() != null && !request.getEventIds().isEmpty()) {
            List<Event> events = eventRepository.findAllByIdIn(request.getEventIds());
            incident.setEvents(new ArrayList<>(events));
        }

        return IncidentResponse.fromEntity(incidentRepository.save(incident));
    }

    private IncidentSeverity calculateSeverity(Integer riskScore) {
        if (riskScore == null) {
            return IncidentSeverity.LOW;
        }
        if (riskScore >= 80) return IncidentSeverity.CRITICAL;
        if (riskScore >= 60) return IncidentSeverity.HIGH;
        if (riskScore >= 40) return IncidentSeverity.MEDIUM;
        return IncidentSeverity.LOW;
    }

    private void validateStatusTransition(IncidentStatus current, IncidentStatus next) {
        boolean valid = switch (current) {
            case OPEN -> next == IncidentStatus.INVESTIGATING || next == IncidentStatus.RESOLVED || next == IncidentStatus.FALSE_POSITIVE;
            case INVESTIGATING -> next == IncidentStatus.RESOLVED || next == IncidentStatus.FALSE_POSITIVE;
            case RESOLVED, FALSE_POSITIVE -> false;
        };

        if (!valid) {
            throw new IllegalArgumentException("Transição de status inválida");
        }
    }
}
