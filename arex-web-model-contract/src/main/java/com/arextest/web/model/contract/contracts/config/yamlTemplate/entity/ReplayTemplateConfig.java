package com.arextest.web.model.contract.contracts.config.yamlTemplate.entity;

import lombok.Data;

import java.util.Collection;
import java.util.Map;


@Data
public class ReplayTemplateConfig {

    private Integer offsetDays;

    private Map<String, Collection<String>> excludeOperationMap;

    // private Set<String> targetEnv;

    // private Integer sendMaxQps;
}
