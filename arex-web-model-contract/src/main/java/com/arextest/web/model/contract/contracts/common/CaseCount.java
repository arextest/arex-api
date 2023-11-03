package com.arextest.web.model.contract.contracts.common;

import lombok.Data;

@Data
public class CaseCount {

  private int totalCaseCount;
  private int receivedCaseCount;
  private int successCaseCount;
  private int failCaseCount;
  private int errorCaseCount;
  private int totalOperationCount;
  private int successOperationCount;
}
