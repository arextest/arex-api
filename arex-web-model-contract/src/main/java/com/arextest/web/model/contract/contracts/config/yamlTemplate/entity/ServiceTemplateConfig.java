package com.arextest.web.model.contract.contracts.config.yamlTemplate.entity;

import lombok.Data;


@Data
public class ServiceTemplateConfig {

    private int sampleRate;

    private int allowDayOfWeeks;

    private String allowTimeOfDayFrom;

    private String allowTimeOfDayTo;
}
