package com.sentinela.incident.dto;

import jakarta.validation.constraints.NotBlank;

public class IncidentNoteRequest {

    @NotBlank(message = "Conteúdo da nota é obrigatório")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
