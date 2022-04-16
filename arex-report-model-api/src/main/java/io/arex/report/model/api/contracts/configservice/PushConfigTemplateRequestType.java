package io.arex.report.model.api.contracts.configservice;

import lombok.Data;


@Data
public class PushConfigTemplateRequestType {
    private String appId;
    private String configTemplate;
}
