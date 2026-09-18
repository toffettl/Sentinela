package com.sentinela.incident.dto;

import com.sentinela.event.entity.Event;

import java.time.LocalDateTime;

public class EventSummary {

    private Long id;
    private String eventType;
    private LocalDateTime timestamp;
    private String user;
    private String ip;
    private String source;

    public static EventSummary fromEntity(Event event) {
        EventSummary summary = new EventSummary();
        summary.setId(event.getId());
        summary.setEventType(event.getEventType().name());
        summary.setTimestamp(event.getTimestamp());
        summary.setUser(event.getUser().getName());
        summary.setIp(event.getIp());
        summary.setSource(event.getSource());
        return summary;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
