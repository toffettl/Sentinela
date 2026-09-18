package com.sentinela.securityrule.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "security_rule")
@Getter
@Setter
@NoArgsConstructor
public class SecurityRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "rule_type", nullable = false, unique = true)
    private RuleType ruleType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "risk_points", nullable = false)
    private int riskPoints;
}
