package com.sentinela.rule.engine;

import com.sentinela.securityrule.entity.RuleType;
import com.sentinela.securityrule.entity.SecurityRule;
import com.sentinela.securityrule.repository.SecurityRuleRepository;
import org.springframework.stereotype.Service;

@Service
public class RuleEngine {

    private static final int INCIDENT_RISK_THRESHOLD = 150;
    private final SecurityRuleRepository securityRuleRepository;

    public RuleEngine(SecurityRuleRepository securityRuleRepository) {
        this.securityRuleRepository = securityRuleRepository;
    }

    public RiskEvaluation evaluate(String pattern, int currentRiskPoints) {
        RuleType ruleType = RuleType.valueOf(pattern);
        SecurityRule securityRule = findRuleFor(ruleType);
        int updatedRiskPoints = currentRiskPoints + securityRule.getRiskPoints();

        return new RiskEvaluation(
                securityRule,
                updatedRiskPoints,
                updatedRiskPoints >= INCIDENT_RISK_THRESHOLD
        );
    }

    public SecurityRule findRuleFor(RuleType ruleType) {
        return securityRuleRepository.findAll().stream()
                .filter(rule -> rule.getRuleType() == ruleType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Regra de segurança não encontrada: " + ruleType));
    }

    public record RiskEvaluation(
            SecurityRule securityRule,
            int riskPoints,
            boolean shouldCreateIncident
    ) {
    }
}
