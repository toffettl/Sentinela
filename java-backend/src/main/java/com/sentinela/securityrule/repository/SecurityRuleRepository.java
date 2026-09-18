package com.sentinela.securityrule.repository;

import com.sentinela.securityrule.entity.SecurityRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityRuleRepository extends JpaRepository<SecurityRule, Long> {
}
