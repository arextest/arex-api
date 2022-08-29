package com.arextest.report.model.api.contracts.common;

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
    
    private String baseMsg;
    
    private String testMsg;
    
    private String planItemId;
    
    private List<LogEntity> logs;
}
