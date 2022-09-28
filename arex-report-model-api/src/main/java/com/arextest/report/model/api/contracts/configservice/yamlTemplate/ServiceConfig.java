package com.arextest.report.model.api.contracts.configservice.yamlTemplate;

import lombok.Data;

import java.util.Collection;


@Data
public class ServiceConfig {

    private int sampleRate;

    private Collection<String> excludeDependentOperationSet;

    private Collection<String> excludeDependentServiceSet;

    private Collection<String> excludeOperationSet;

    private Collection<String> includeServiceSet;

    private Collection<String> includeOperationSet;

    private int allowDayOfWeeks;

    private String allowTimeOfDayFrom;

    private String allowTimeOfDayTo;
}
