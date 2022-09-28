package com.arextest.report.model.api.contracts.configservice.yamlTemplate;

import lombok.Data;

import java.util.List;


@Data
public class YamlTemplate {
    private RecordConfig recordConfig;
    private ReplayConfig replayConfig;
    private List<OperationCompareConfig> compareConfig;
}
