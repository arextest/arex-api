package com.arextest.web.model.contract.contracts.config.yamlTemplate.entity;

import lombok.Data;

import java.util.List;


@Data
public class YamlTemplate {
    private RecordTemplateConfig recordTemplateConfig;
    private ReplayTemplateConfig replayTemplateConfig;
    private List<OperationCompareTemplateConfig> compareTemplateConfigs;
}
