package com.sentinela.asset.dto;

import com.sentinela.asset.entity.Asset;
import com.sentinela.asset.entity.AssetStatus;
import java.time.LocalDateTime;

public class AssetResponse {

    private Long id;
    private String name;
    private String hostname;
    private String ip;
    private String operatingSystem;
    private AssetStatus status;
    private LocalDateTime createdAt;

    public static AssetResponse fromEntity(Asset asset) {
        AssetResponse response = new AssetResponse();
        response.setId(asset.getId());
        response.setName(asset.getName());
        response.setHostname(asset.getHostname());
        response.setIp(asset.getIp());
        response.setOperatingSystem(asset.getOperatingSystem());
        response.setStatus(asset.getStatus());
        response.setCreatedAt(asset.getCreatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public AssetStatus getStatus() {
        return status;
    }

    public void setStatus(AssetStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
