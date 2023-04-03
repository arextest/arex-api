package com.arextest.web.model.dao.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;


@Data
@Document(collection = "ReportPlanStatistic")
public class ReportPlanStatisticCollection extends ModelBase{
    
    
    private String planId;
    
    private Integer status;
    private String appId;
    private String appName;
    
    private Boolean approvePassed;
    private String approveReason;
    private String approveOperator;

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
    private Map<String, Object> customTags;
    // private Integer errorCaseCount;
    // private Integer successCaseCount;
    // private Integer failCaseCount;
    // private Integer waitCaseCount;
    //
    // private Integer totalOperationCount;
    // private Integer errorOperationCount;
    // private Integer successOperationCount;
    // private Integer failOperationCount;
    // private Integer waitOperationCount;
    //
    // private Integer totalServiceCount;
}
