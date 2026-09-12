package com.sentinela.asset.service;

import com.sentinela.asset.dto.AssetRequest;
import com.sentinela.asset.dto.AssetResponse;
import com.sentinela.asset.entity.Asset;
import com.sentinela.asset.entity.AssetStatus;
import com.sentinela.asset.repository.AssetRepository;
import com.sentinela.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetService {

    private final AssetRepository assetRepository;

    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    public List<AssetResponse> findAll() {
        return assetRepository.findAll().stream()
                .map(AssetResponse::fromEntity)
                .toList();
    }

    public AssetResponse findById(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ativo não encontrado"));
        return AssetResponse.fromEntity(asset);
    }

    public AssetResponse create(AssetRequest request) {
        Asset asset = new Asset();
        asset.setName(request.getName());
        asset.setHostname(request.getHostname());
        asset.setIp(request.getIp());
        asset.setOperatingSystem(request.getOperatingSystem());
        asset.setStatus(request.getStatus() != null ? request.getStatus() : AssetStatus.ACTIVE);

        return AssetResponse.fromEntity(assetRepository.save(asset));
    }

    public AssetResponse update(Long id, AssetRequest request) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ativo não encontrado"));

        asset.setName(request.getName());
        asset.setHostname(request.getHostname());
        asset.setIp(request.getIp());
        asset.setOperatingSystem(request.getOperatingSystem());
        asset.setStatus(request.getStatus() != null ? request.getStatus() : asset.getStatus());

        return AssetResponse.fromEntity(assetRepository.save(asset));
    }

    public void delete(Long id) {
        if (!assetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ativo não encontrado");
        }

        assetRepository.deleteById(id);
    }
}
