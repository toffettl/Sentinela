package com.sentinela.event.dto;

import com.sentinela.asset.entity.Asset;
import com.sentinela.event.entity.EventType;
import com.sentinela.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class EventRequest {

    @NotBlank(message = "Source é obrigatório")
    private String source;

    @NotBlank(message = "IP é obrigatório")
    private String ip;

    @NotBlank(message = "EventType é obrigatório")
    private EventType eventType;

    @NotBlank(message = "Timestamp é obrigatório")
    private Timestamp timestamp;

    @NotBlank(message = "AssetId é obrigatório")
    private Long assetId;

    @NotBlank(message = "UserId é obrigatório")
    private Long userId;
}
