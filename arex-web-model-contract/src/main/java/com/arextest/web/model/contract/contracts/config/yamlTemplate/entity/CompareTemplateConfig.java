package com.arextest.web.model.contract.contracts.config.yamlTemplate.entity;

import lombok.Data;

import java.util.List;


@Data
public class CompareTemplateConfig {
    List<OperationCompareTemplateConfig> operationCompareTemplateConfigs;
}
