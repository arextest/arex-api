package io.arex.report.model.api.contracts.common;

import lombok.Data;

import java.util.List;


@Data
public class CompareResult {
    
    private Long planId;
    
    private Long operationId;
    
    private String serviceName;
    
    private String categoryName;
    
    private Integer diffResultCode;
    
    private String operationName;
    
    private String replayId;
    
    private String recordId;
    
    private String baseMsg;
    
    private String testMsg;
    
    private Long planItemId;
    
    private List<LogEntity> logs;
}
