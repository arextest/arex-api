package io.arex.report.model.dao.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;


@Data
@Document(collection = "ReportPlanItemStatistic")
public class ReportPlanItemStatisticCollection {
    
    private Long planItemId;
    
    private Long planId;
    
    private Long operationId;
    
    private String operationName;
    
    private String serviceName;
    
    private Integer status;
    
    private Long replayStartTime;
    private Long replayEndTime;
    
    private Integer totalCaseCount;

    // key: replayId   value:count
    private Map<String, Integer> cases;
    private Map<String, Integer> failCases;
    private Map<String, Integer> errorCases;
}
