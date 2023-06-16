package com.arextest.web.model.contract.contracts.config.yamlTemplate.entity;

import lombok.Data;

import java.util.Collection;

/**
 * @see com.arextest.web.model.contract.contracts.config.record.ServiceCollectConfiguration
 */
@Data
public class ServiceTemplateConfig {

    private int sampleRate;

    private int allowDayOfWeeks;

    private boolean timeMock;

    private String allowTimeOfDayFrom;

    private String allowTimeOfDayTo;

    private Collection<String> excludeServiceOperationSet;

    private Integer recordMachineCountLimit;
}
