package com.sentinela.detection.repository;

import com.sentinela.detection.entity.Detection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DetectionRepository extends JpaRepository<Detection, Long> {
    @Query("select coalesce(sum(d.riskPoints), 0) from Detection d where d.ip = :ip")
    int sumRiskPointsByIp(@Param("ip") String ip);
}
