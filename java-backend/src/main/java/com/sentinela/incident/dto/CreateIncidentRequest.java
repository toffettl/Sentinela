package com.sentinela.incident.dto;

import java.util.List;

public class CreateIncidentRequest {

    private String title;
    private String description;
    private String severity;
    private Integer riskScore;
    private String userInvolved;
    private String ipInvolved;
    private String assetInvolved;
    private List<Long> eventIds;
    private List<String> rulesTriggered;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public String getUserInvolved() {
        return userInvolved;
    }

    public void setUserInvolved(String userInvolved) {
        this.userInvolved = userInvolved;
    }

    public String getIpInvolved() {
        return ipInvolved;
    }

    public void setIpInvolved(String ipInvolved) {
        this.ipInvolved = ipInvolved;
    }

    public String getAssetInvolved() {
        return assetInvolved;
    }

    public void setAssetInvolved(String assetInvolved) {
        this.assetInvolved = assetInvolved;
    }

    public List<Long> getEventIds() {
        return eventIds;
    }

    public void setEventIds(List<Long> eventIds) {
        this.eventIds = eventIds;
    }

    public List<String> getRulesTriggered() {
        return rulesTriggered;
    }

    public void setRulesTriggered(List<String> rulesTriggered) {
        this.rulesTriggered = rulesTriggered;
    }
}
