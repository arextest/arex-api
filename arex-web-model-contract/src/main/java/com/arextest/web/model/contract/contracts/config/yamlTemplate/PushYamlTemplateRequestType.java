package com.arextest.web.model.contract.contracts.config.yamlTemplate;

import lombok.Data;

@Data
public class PushYamlTemplateRequestType {

  private String appId;
  private String configTemplate;
}
