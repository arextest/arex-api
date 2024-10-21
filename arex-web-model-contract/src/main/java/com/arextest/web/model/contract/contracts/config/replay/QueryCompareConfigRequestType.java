package com.arextest.web.model.contract.contracts.config.replay;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QueryCompareConfigRequestType {

  @NotBlank(message = "appId can not be blank")
  private String appId;

  private String operationName;

}
