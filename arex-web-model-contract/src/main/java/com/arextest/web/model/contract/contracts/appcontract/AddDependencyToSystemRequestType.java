package com.arextest.web.model.contract.contracts.appcontract;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddDependencyToSystemRequestType {

  @NotBlank(message = "operationId cannot be blank")
  private String appId;
  @NotBlank(message = "operationId cannot be blank")
  private String operationId;
  @NotBlank(message = "operationType cannot be blank")
  private String operationType;
  @NotBlank(message = "operationName cannot be blank")
  private String operationName;
  private String msg;
}
