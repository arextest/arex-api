package io.arex.report.model.dto;

import io.arex.report.model.api.contracts.common.LogEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class CompareResultDto extends BaseDto {
    
    private Long planId;
    
    private Long operationId;
    
    private String serviceName;
    
    private String categoryName;
    
    private String operationName;
    
    private String replayId;
    
    private String recordId;
    
    private String baseMsg;
    
    private String testMsg;
    
    private List<LogEntity> logs;
    // planItemId
    private Long planItemId;
    
    private Integer diffResultCode;
    
    private Date dataCreateTime;
}
