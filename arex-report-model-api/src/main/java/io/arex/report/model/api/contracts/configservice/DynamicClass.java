package io.arex.report.model.api.contracts.configservice;

import lombok.Data;


@Data
public class DynamicClass {
    private String fullClassName;
    private String methodName;
    private String parameterTypes;
    private String keyFormula;
}
