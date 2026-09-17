package com.sentinela.rust;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public record RustEventRequest(
        UUID id,
        @JsonProperty("event_type") String eventType,
        Instant timestamp,
        String source,
        String user,
        String ip,
        String asset
) {
}