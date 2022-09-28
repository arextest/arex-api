package com.arextest.report.model.api.contracts.configservice;

import com.arextest.report.model.api.contracts.configservice.yamlTemplate.OperationCompareConfig;
import lombok.Data;

import java.util.List;


@Data
public class CompareConfig {
    List<OperationCompareConfig> operationCompareConfigs;
}
