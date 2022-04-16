package io.arex.report.model.api.contracts.common;

import lombok.Data;


@Data
public class PlanItemStatistic {
    
    private Long planItemId;
    
    private Long planId;
    
    private Long operationId;
    
    private String operationName;
    
    private String serviceName;
    private String appId;

    
    private Integer status;
    
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
