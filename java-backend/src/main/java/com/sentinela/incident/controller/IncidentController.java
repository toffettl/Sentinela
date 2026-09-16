package com.sentinela.incident.controller;

import com.sentinela.incident.dto.IncidentNoteRequest;
import com.sentinela.incident.dto.IncidentNoteResponse;
import com.sentinela.incident.dto.IncidentResponse;
import com.sentinela.incident.dto.IncidentUpdateRequest;
import com.sentinela.incident.dto.EventSummary;
import com.sentinela.incident.entity.IncidentSeverity;
import com.sentinela.incident.entity.IncidentStatus;
import com.sentinela.incident.service.IncidentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping
    public List<IncidentResponse> list(
            @RequestParam(required = false) IncidentStatus status,
            @RequestParam(required = false) IncidentSeverity severity,
            @RequestParam(required = false) String user
    ) {
        return incidentService.findAll(status, severity, user);
    }

    @GetMapping("/{id}")
    public IncidentResponse findById(@PathVariable Long id) {
        return incidentService.findById(id);
    }

    @GetMapping("/{id}/events")
    public List<EventSummary> listEvents(@PathVariable Long id) {
        return incidentService.findEventsByIncidentId(id);
    }

    @PatchMapping("/{id}")
    public IncidentResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody IncidentUpdateRequest request
    ) {
        return incidentService.updateStatus(id, request);
    }

    @PostMapping("/{id}/notes")
    @ResponseStatus(HttpStatus.CREATED)
    public IncidentNoteResponse addNote(
            @PathVariable Long id,
            @Valid @RequestBody IncidentNoteRequest request
    ) {
        return incidentService.addNote(id, request);
    }
}
