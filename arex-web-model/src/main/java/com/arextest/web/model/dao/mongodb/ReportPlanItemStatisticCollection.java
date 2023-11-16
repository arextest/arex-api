package com.arextest.web.model.dao.mongodb;

import java.util.Map;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "ReportPlanItemStatistic")
@FieldNameConstants
public class ReportPlanItemStatisticCollection {

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

  // key: replayId value:count
  private Map<String, Integer> cases;
  private Map<String, Integer> failCases;
  private Map<String, Integer> errorCases;
}
