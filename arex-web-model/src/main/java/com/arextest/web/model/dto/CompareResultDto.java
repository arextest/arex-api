package com.arextest.web.model.dto;

import com.arextest.web.model.contract.contracts.common.LogEntity;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class CompareResultDto extends BaseDto {

  private String planId;

  private String operationId;

  private String serviceName;

  private String categoryName;

  private String operationName;

  private String replayId;

  private String recordId;

  private long recordTime;

  private long replayTime;

  private String instanceId;

  private String baseMsg;

  private String testMsg;

  private List<LogEntity> logs;
  // planItemId
  private String planItemId;

  private Integer diffResultCode;

  private MsgInfoDto msgInfo;

  private Boolean ignore;

  private Date dataCreateTime;
}
