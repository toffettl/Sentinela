package com.sentinela.detection.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record DetectionRequest(
        @NotBlank String pattern,
        @NotBlank String ip,
        @JsonProperty("event_count") int eventCount,
        @NotEmpty @JsonProperty("event_ids") List<String> eventIds
) {
}
