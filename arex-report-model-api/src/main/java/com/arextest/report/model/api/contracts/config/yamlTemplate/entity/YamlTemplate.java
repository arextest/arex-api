package com.arextest.report.model.api.contracts.config.yamlTemplate.entity;

import lombok.Data;

import java.util.List;


@Data
public class YamlTemplate {
    private RecordTemplateConfig recordTemplateConfig;
    private ReplayTemplateConfig replayTemplateConfig;
    private List<OperationCompareTemplateConfig> compareTemplateConfigs;
}
