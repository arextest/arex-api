package com.arextest.web.model.contract.contracts.config.yamlTemplate.entity;

import java.util.List;

import lombok.Data;

@Data
public class YamlTemplate {
    private RecordTemplateConfig recordTemplateConfig;
    private ReplayTemplateConfig replayTemplateConfig;
    private List<OperationCompareTemplateConfig> compareTemplateConfigs;
}
