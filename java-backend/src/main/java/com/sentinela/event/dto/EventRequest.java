package com.sentinela.event.dto;

import com.sentinela.asset.entity.Asset;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sentinela.event.entity.EventType;
import com.sentinela.user.entity.User;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
public class EventRequest {

    @Id
    private Long id;

    @NotBlank(message = "Source é obrigatório")
    private String source;

    @NotBlank(message = "IP é obrigatório")
    private String ip;

    private String user;

    private String asset;

    @NotNull(message = "EventType é obrigatório")
    @JsonProperty("event_type")
    private EventType eventType;

    @NotNull(message = "Timestamp é obrigatório")
    private LocalDateTime timestamp;

    private Long assetId;

    private Long userId;
}
