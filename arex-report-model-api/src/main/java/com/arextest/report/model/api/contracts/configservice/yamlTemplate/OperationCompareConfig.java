package com.arextest.report.model.api.contracts.configservice.yamlTemplate;

import lombok.Data;

import java.util.Collection;

/**
 * Created by rchen9 on 2022/9/27.
 */
@Data
public class OperationCompareConfig {
    private String operationName;

    private Collection<String> exclusions;
    private Collection<String> inclusions;
    private Collection<ListSortConfig> listSort;
    private Collection<ReferenceConfig> references;
}
