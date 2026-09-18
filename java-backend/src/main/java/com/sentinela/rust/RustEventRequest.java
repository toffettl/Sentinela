package com.sentinela.rust;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record RustEventRequest(
        UUID id,
        @JsonProperty("event_type") String eventType,
        LocalDateTime timestamp,
        String source,
        String user,
        String ip,
        String asset
) {
}