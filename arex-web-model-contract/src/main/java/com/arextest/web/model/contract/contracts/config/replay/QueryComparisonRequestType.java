package com.arextest.web.model.contract.contracts.config.replay;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QueryComparisonRequestType {

  @NotNull(message = "appid cannot be null")
  private String appId;
  private String operationId;

  /**
   * operationName and operationType are the key of dependency
   */
  private String operationName;
  private String operationType;
  private Boolean filterExpired;
}
