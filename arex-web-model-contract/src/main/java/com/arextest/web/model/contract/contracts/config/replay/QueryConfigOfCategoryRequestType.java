package com.arextest.web.model.contract.contracts.config.replay;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class QueryConfigOfCategoryRequestType {
  @NotBlank(message = "appId can not be blank")
  private String appId;

  private String operationName;

  private String categoryName;

  @NotEmpty(message = "entryPoint can not be empty")
  private Boolean entryPoint;

}