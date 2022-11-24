package com.arextest.web.model.dto;

import com.arextest.web.model.contract.contracts.common.LogEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class CompareResultDto extends BaseDto {
    
    private String planId;
    
    private String operationId;
    
    private String serviceName;
    
    private String categoryName;
    
    private String operationName;
    
    private String replayId;
    
    private String recordId;
    
    private String baseMsg;
    
    private String testMsg;
    
    private List<LogEntity> logs;
    // planItemId
    private String planItemId;
    
    private Integer diffResultCode;
    
    private Date dataCreateTime;
}
