package com.sentinela.incident.dto;

import com.sentinela.incident.entity.IncidentStatus;
import jakarta.validation.constraints.NotNull;

public class IncidentUpdateRequest {

    @NotNull(message = "Status é obrigatório")
    private IncidentStatus status;

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }
}
