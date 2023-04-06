package com.arextest.web.model.dao.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;


@Data
@Document(collection = "ReportPlanItemStatistic")
public class ReportPlanItemStatisticCollection {
    
    private String planItemId;
    
    private String planId;
    
    private String operationId;
    
    private String operationName;
    
    private String serviceName;
    
    private Integer status;
    private String errorMessage;
    
    private Long replayStartTime;
    private Long replayEndTime;
    
    private Integer totalCaseCount;

    // key: replayId   value:count
    private Map<String, Integer> cases;
    private Map<String, Integer> failCases;
    private Map<String, Integer> errorCases;
}
