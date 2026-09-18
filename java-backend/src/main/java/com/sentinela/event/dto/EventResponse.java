package com.sentinela.event.dto;

import com.sentinela.event.entity.Event;
import com.sentinela.event.entity.EventType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventResponse {

    private Long id;

    @JsonProperty("event_type")
    private EventType eventType;
    private LocalDateTime timestamp;
    private String source;
    private String user;
    private String ip;
    private String asset;

    public static EventResponse fromEntity(Event event) {
        EventResponse response = new EventResponse();
        response.setId(event.getId());
        response.setEventType(event.getEventType());
        response.setTimestamp(event.getTimestamp());
        response.setSource(event.getSource());
        response.setUser(event.getUser().getName());
        response.setIp(event.getIp());
        response.setAsset(event.getAsset().getName());
        return response;
    }
}
