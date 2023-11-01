package com.arextest.web.model.contract.contracts.config.yamlTemplate.entity;

import java.util.List;

import lombok.Data;

@Data
public class CompareTemplateConfig {
    List<OperationCompareTemplateConfig> operationCompareTemplateConfigs;
}
