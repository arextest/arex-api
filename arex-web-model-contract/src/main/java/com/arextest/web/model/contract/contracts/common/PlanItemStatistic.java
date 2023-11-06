package com.arextest.web.model.contract.contracts.common;

import lombok.Data;

@Data
public class PlanItemStatistic {

  private String planItemId;

  private String planId;

  private String operationId;

  private String operationName;

  private String serviceName;
  private String appId;
  private String appName;

  private Integer status;
  private String errorMessage;

  private Long replayStartTime;

  private Long replayEndTime;

  private String sourceHost;

  private String sourceEnv;

  private String targetHost;

  private String targetEnv;

  private Integer caseSourceType;

  private Long caseStartTime;

  private Long caseEndTime;

  private Integer totalCaseCount;
  private Integer errorCaseCount;
  private Integer successCaseCount;
  private Integer failCaseCount;
  private Integer waitCaseCount;
}
