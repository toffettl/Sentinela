package com.sentinela.event.dto;

import com.sentinela.asset.entity.Asset;
import com.sentinela.event.entity.EventType;
import com.sentinela.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;

@Getter
@Setter
public class EventRequest {

    @NotBlank(message = "Source é obrigatório")
    private String source;

    @NotBlank(message = "IP é obrigatório")
    private String ip;

    @NotNull(message = "EventType é obrigatório")
    private EventType eventType;

    @NotNull(message = "Timestamp é obrigatório")
    private Instant timestamp;

    @NotNull(message = "AssetId é obrigatório")
    private Long assetId;

    @NotNull(message = "UserId é obrigatório")
    private Long userId;
}
