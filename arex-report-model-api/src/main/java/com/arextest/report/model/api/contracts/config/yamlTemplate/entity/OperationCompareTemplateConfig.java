package com.arextest.report.model.api.contracts.config.yamlTemplate.entity;

import lombok.Data;

import java.util.Collection;

/**
 * Created by rchen9 on 2022/9/27.
 */
@Data
public class OperationCompareTemplateConfig {
    private String operationName;

    private Collection<String> exclusions;
    private Collection<String> inclusions;
    private Collection<ListSortTemplateConfig> listSort;
    private Collection<ReferenceTemplateConfig> references;
}
