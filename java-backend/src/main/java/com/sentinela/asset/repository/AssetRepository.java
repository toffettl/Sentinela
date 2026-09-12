package com.sentinela.asset.repository;

import com.sentinela.asset.entity.Asset;
import com.sentinela.asset.entity.AssetStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    long countByStatus(AssetStatus status);
}
