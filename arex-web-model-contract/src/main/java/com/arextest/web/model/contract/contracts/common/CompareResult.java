package com.arextest.web.model.contract.contracts.common;

import lombok.Data;

import java.util.List;


@Data
public class CompareResult {
    
    private String planId;
    
    private String operationId;
    
    private String serviceName;
    
    private String categoryName;
    
    private Integer diffResultCode;
    
    private String operationName;
    
    private String replayId;
    
    private String recordId;

    private long recordTime;

    private long replayTime;
    
    private String instanceId;
    
    private String baseMsg;
    
    private String testMsg;
    
    private String planItemId;
    
    private List<LogEntity> logs;
}
