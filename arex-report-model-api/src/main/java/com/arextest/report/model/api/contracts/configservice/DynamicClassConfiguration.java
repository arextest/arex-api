package com.arextest.report.model.api.contracts.configservice;

import lombok.Data;


@Data
public class DynamicClassConfiguration {
    private String id;
    private String appId;
    private String fullClassName;
    private String methodName;
    private String parameterTypes;
    private String keyFormula;
}
