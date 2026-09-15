package com.sentinela.event.dto;

import com.sentinela.event.entity.Event;
import com.sentinela.event.entity.EventType;
import com.sentinela.user.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
public class EventResponse {

    private Long id;
    private EventType eventType;
    private Timestamp timestamp;
    private String source;
    private String ip;
    private Long assetId;
    private Long userId;

    public static EventResponse fromEntity(Event event) {
        EventResponse response = new EventResponse();
        response.setEventType(event.getEventType());
        response.setTimestamp(event.getTimestamp());
        response.setSource(event.getSource());
        response.setIp(event.getIp());
        response.setAssetId(event.getAsset().getId());
        return response;
    }
}
