package com.arextest.web.model.contract.contracts;

import lombok.Data;

@Data
public class OverwriteContractRequestType {

  private String appId;

  private String operationId;

  /**
   * the key of dependency
   */
  private String operationName;
  private String operationType;

  private String operationResponse;
}
