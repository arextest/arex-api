package com.arextest.web.model.contract.contracts.environment;

import lombok.Data;

@Data
public class SaveEnvironmentResponseType {

  private Boolean success;
  private String environmentId;
}
