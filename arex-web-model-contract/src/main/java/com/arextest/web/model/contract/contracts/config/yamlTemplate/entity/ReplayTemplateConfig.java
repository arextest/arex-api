package com.arextest.web.model.contract.contracts.config.yamlTemplate.entity;

import java.util.Collection;
import java.util.Map;

import lombok.Data;

@Data
public class ReplayTemplateConfig {

    private Integer offsetDays;

    private Map<String, Collection<String>> excludeOperationMap;

    // private Set<String> targetEnv;

    private Integer sendMaxQps;
}
