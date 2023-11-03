package com.arextest.web.model.contract.contracts.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class AppCaseResult {

  private String appId;

  private String appName;

  private Integer totalCaseCount;

  private Integer successCaseCount;

  private Integer errorCaseCount;

  private Long createTime;

  @JsonIgnore
  private String date;
}
