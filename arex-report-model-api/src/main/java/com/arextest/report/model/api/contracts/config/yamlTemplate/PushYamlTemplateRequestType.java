package com.arextest.report.model.api.contracts.config.yamlTemplate;

import lombok.Data;


@Data
public class PushYamlTemplateRequestType {
    private String appId;
    private String configTemplate;
}
