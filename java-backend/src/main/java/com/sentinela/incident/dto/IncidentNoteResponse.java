package com.sentinela.incident.dto;

import com.sentinela.incident.entity.IncidentNote;

import java.time.LocalDateTime;

public class IncidentNoteResponse {

    private Long id;
    private String content;
    private LocalDateTime createdAt;

    public static IncidentNoteResponse fromEntity(IncidentNote note) {
        IncidentNoteResponse response = new IncidentNoteResponse();
        response.setId(note.getId());
        response.setContent(note.getContent());
        response.setCreatedAt(note.getCreatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
