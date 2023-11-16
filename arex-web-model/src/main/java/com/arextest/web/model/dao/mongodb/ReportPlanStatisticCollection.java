package com.arextest.web.model.dao.mongodb;

import java.util.Map;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "ReportPlanStatistic")
@FieldNameConstants
public class ReportPlanStatisticCollection extends ModelBase {

  private String planId;

  private Integer status;
  private String appId;
  private String appName;

  private Boolean approvePassed;
  private String approveReason;
  private String approveOperator;

  private String planName;
  private String creator;
  private String targetImageId;
  private String targetImageName;

  private Integer caseSourceType;
  private String sourceEnv;
  private String targetEnv;
  private String sourceHost;
  private String targetHost;

  private String coreVersion;

  private String extVersion;

  private String caseRecordVersion;

  private Long replayStartTime;
  private Long replayEndTime;
  private Long caseStartTime;
  private Long caseEndTime;

  private Integer totalCaseCount;
  private String errorMessage;
  private Map<String, Object> customTags;
}
