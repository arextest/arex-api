package com.arextest.web.model.dto;

import java.util.Date;
import java.util.Map;
import lombok.Data;

@Data
public class PlanItemDto {

  private String planItemId;

  private String planId;

  private String operationId;

  private String operationName;

  private String serviceName;

  private Integer status;
  private String errorMessage;

  private Long replayStartTime;
  private Long replayEndTime;

  private Integer totalCaseCount;

  private Date dataCreateTime;

  private Map<String, Integer> cases;

  private Map<String, Integer> failCases;

  private Map<String, Integer> errorCases;
}
