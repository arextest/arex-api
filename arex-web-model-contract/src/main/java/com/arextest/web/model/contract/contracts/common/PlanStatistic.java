package com.arextest.web.model.contract.contracts.common;

import lombok.Data;


@Data
public class PlanStatistic {
    
    
    private String planId;
    
    private String planName;
    
    private Integer status;
    private String appId;
    private String appName;

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
    private Integer errorOperationCount;
    private Integer successOperationCount;
    private Integer failOperationCount;
    private Integer waitOperationCount;
    
    private Integer totalServiceCount;
}
