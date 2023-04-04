package com.arextest.web.model.dto;

import lombok.Data;

import java.util.Map;


@Data
public class ReportPlanStatisticDto {
    private String planId;

    private Integer status;
    private String appId;
    private String appName;

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
    private Integer errorCaseCount;
    private Integer successCaseCount;
    private Integer failCaseCount;
    private Integer waitCaseCount;

    private Integer totalOperationCount;
    private Integer successOperationCount;
    private Long dataChangeCreateTime;
    private String errorMessage;

    private Map<String, Object> customTags;
}
