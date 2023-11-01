package com.arextest.web.model.contract.contracts;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class ReportInitialRequestType {

  private String planId;

  private String planName;

  private Application application;
  private String creator;
  private HostEnvironment hostEnv;
  private CaseSourceEnvironment caseSourceEnv;
  private TargetImage targetImage;
  private Version version;
  private Map<String, Object> customTags;

  private Integer totalCaseCount;

  private List<ReportItem> reportItemList;

  @Data
  public static class Application {

    private String appId;
    private String appName;
  }

  @Data
  public static class HostEnvironment {

    private String sourceEnv;
    private String targetEnv;
    private String sourceHost;
    private String targetHost;
  }

  @Data
  public static class CaseSourceEnvironment {

    private Integer caseSourceType;

    private Long caseStartTime;

    private Long caseEndTime;
  }

  @Data
  public static class TargetImage {

    private String targetImageId;
    private String targetImageName;
  }

  @Data
  public static class ReportItem {

    private String planItemId;
    private String operationId;

    private String operationName;
    private String serviceName;

    private Integer totalCaseCount;
  }

  @Data
  public static class Version {

    private String coreVersion;

    private String extVersion;

    private String caseRecordVersion;
  }
}
