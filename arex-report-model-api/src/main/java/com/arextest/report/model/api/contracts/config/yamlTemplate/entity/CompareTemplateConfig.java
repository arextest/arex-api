package com.arextest.report.model.api.contracts.config.yamlTemplate.entity;

import lombok.Data;

import java.util.List;


@Data
public class CompareTemplateConfig {
    List<OperationCompareTemplateConfig> operationCompareTemplateConfigs;
}
