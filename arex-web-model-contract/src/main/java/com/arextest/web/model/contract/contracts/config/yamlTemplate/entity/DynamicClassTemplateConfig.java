package com.arextest.web.model.contract.contracts.config.yamlTemplate.entity;

import lombok.Data;

@Data
public class DynamicClassTemplateConfig {
    private String fullClassName;
    private String methodName;
    private String parameterTypes;
}
