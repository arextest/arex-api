package com.arextest.report.model.api.contracts.config.yamlTemplate;

import lombok.Data;

import java.util.Collection;
import java.util.Map;


@Data
public class ServiceConfig {

    private int sampleRate;

    private Map<String, Collection<String>> excludeOperationMap;

    private int allowDayOfWeeks;

    private String allowTimeOfDayFrom;

    private String allowTimeOfDayTo;
}
