package com.arextest.web.model.contract.contracts;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SyncResponseContractRequestType {

  private String appId;
  @NotNull(message = "operationId cannot be empty")
  private String operationId;
}
