package com.sentinela.detection.service;

import com.sentinela.detection.dto.DetectionRequest;
import com.sentinela.detection.dto.DetectionResponse;
import com.sentinela.detection.entity.Detection;
import com.sentinela.detection.repository.DetectionRepository;
import com.sentinela.incident.entity.Incident;
import com.sentinela.incident.entity.IncidentSeverity;
import com.sentinela.incident.entity.IncidentStatus;
import com.sentinela.incident.repository.IncidentRepository;
import com.sentinela.rule.engine.RuleEngine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class DetectionService {
    private final DetectionRepository detectionRepository;
    private final IncidentRepository incidentRepository;
    private final RuleEngine ruleEngine;

    public DetectionService(DetectionRepository detectionRepository,
                            IncidentRepository incidentRepository,
                            RuleEngine ruleEngine) {
        this.detectionRepository = detectionRepository;
        this.incidentRepository = incidentRepository;
        this.ruleEngine = ruleEngine;
    }

    @Transactional
    public DetectionResponse receive(DetectionRequest request) {
        int currentRiskPoints = detectionRepository.sumRiskPointsByIp(request.ip());
        RuleEngine.RiskEvaluation evaluation = ruleEngine.evaluate(request.pattern(), currentRiskPoints);

        Detection detection = new Detection();
        detection.setPattern(request.pattern());
        detection.setIp(request.ip());
        detection.setEventCount(request.eventCount());
        detection.setEventIds(request.eventIds().stream().collect(Collectors.joining(",")));
        detection.setRiskPoints(evaluation.securityRule().getRiskPoints());
        detectionRepository.save(detection);

        if (evaluation.shouldCreateIncident()) {
            Incident incident = new Incident();
            incident.setTitle("Detecção de " + request.pattern());
            incident.setDescription("Risk Points atingiram o limite de incidente.");
            incident.setRiskScore(evaluation.riskPoints());
            incident.setSeverity(IncidentSeverity.CRITICAL);
            incident.setStatus(IncidentStatus.OPEN);
            incident.setIpInvolved(request.ip());
            incidentRepository.save(incident);
        }

        return DetectionResponse.fromRequest(request);
    }
}
