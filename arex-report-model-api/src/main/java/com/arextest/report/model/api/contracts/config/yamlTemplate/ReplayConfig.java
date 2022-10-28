package com.arextest.report.model.api.contracts.config.yamlTemplate;

import lombok.Data;

import java.util.Collection;
import java.util.Map;


@Data
public class ReplayConfig {

    private Integer offsetDays;

    private Map<String, Collection<String>> excludeOperationMap;

    // private Set<String> targetEnv;

    // private Integer sendMaxQps;
}
