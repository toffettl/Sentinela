package com.sentinela.incident.dto;

import com.sentinela.event.entity.Event;
import com.sentinela.incident.entity.Incident;
import com.sentinela.incident.entity.IncidentSeverity;
import com.sentinela.incident.entity.IncidentStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class IncidentResponse {

    private Long id;
    private String title;
    private String description;
    private IncidentSeverity severity;
    private Integer riskScore;
    private IncidentStatus status;
    private String userInvolved;
    private String ipInvolved;
    private String assetInvolved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<EventSummary> events;
    private List<IncidentNoteResponse> notes;
    private List<String> rulesTriggered;

    public static IncidentResponse fromEntity(Incident incident) {
        IncidentResponse response = new IncidentResponse();
        response.setId(incident.getId());
        response.setTitle(incident.getTitle());
        response.setDescription(incident.getDescription());
        response.setSeverity(incident.getSeverity());
        response.setRiskScore(incident.getRiskScore());
        response.setStatus(incident.getStatus());
        response.setUserInvolved(incident.getUserInvolved());
        response.setIpInvolved(incident.getIpInvolved());
        response.setAssetInvolved(incident.getAssetInvolved());
        response.setCreatedAt(incident.getCreatedAt());
        response.setUpdatedAt(incident.getUpdatedAt());
        response.setEvents(new ArrayList<>());
        response.setNotes(new ArrayList<>());
        response.setRulesTriggered(new ArrayList<>());

        if (incident.getEvents() != null) {
            response.setEvents(incident.getEvents().stream().map(EventSummary::fromEntity).toList());
        }
        if (incident.getNotes() != null) {
            response.setNotes(incident.getNotes().stream().map(IncidentNoteResponse::fromEntity).toList());
        }
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IncidentSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(IncidentSeverity severity) {
        this.severity = severity;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public String getUserInvolved() {
        return userInvolved;
    }

    public void setUserInvolved(String userInvolved) {
        this.userInvolved = userInvolved;
    }

    public String getIpInvolved() {
        return ipInvolved;
    }

    public void setIpInvolved(String ipInvolved) {
        this.ipInvolved = ipInvolved;
    }

    public String getAssetInvolved() {
        return assetInvolved;
    }

    public void setAssetInvolved(String assetInvolved) {
        this.assetInvolved = assetInvolved;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<EventSummary> getEvents() {
        return events;
    }

    public void setEvents(List<EventSummary> events) {
        this.events = events;
    }

    public List<IncidentNoteResponse> getNotes() {
        return notes;
    }

    public void setNotes(List<IncidentNoteResponse> notes) {
        this.notes = notes;
    }

    public List<String> getRulesTriggered() {
        return rulesTriggered;
    }

    public void setRulesTriggered(List<String> rulesTriggered) {
        this.rulesTriggered = rulesTriggered;
    }
}
