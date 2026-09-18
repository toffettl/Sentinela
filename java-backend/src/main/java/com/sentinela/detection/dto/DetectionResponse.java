package com.sentinela.detection.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record DetectionResponse(
        String pattern,
        String ip,
        @JsonProperty("event_count") int eventCount,
        @JsonProperty("event_ids") List<String> eventIds
        // RiskScore
) {
    public static DetectionResponse fromRequest(DetectionRequest request) {
        return new DetectionResponse(
                request.pattern(),
                request.ip(),
                request.eventCount(),
                request.eventIds()
        );
    }
}
