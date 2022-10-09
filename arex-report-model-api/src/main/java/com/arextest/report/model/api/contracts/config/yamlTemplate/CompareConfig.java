package com.arextest.report.model.api.contracts.config.yamlTemplate;

import lombok.Data;

import java.util.List;


@Data
public class CompareConfig {
    List<OperationCompareConfig> operationCompareConfigs;
}
