package com.arextest.report.model.dao.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Data
@Document(collection = "ReplayCompareResult")
public class ReplayCompareResultCollection extends ModelBase {
    
    private String planId;
    
    private String operationId;
    
    private String serviceName;
    
    private String categoryName;
    
    private String operationName;
    
    private String replayId;
    
    private String recordId;
    
    private String baseMsg;
    
    private String testMsg;
    
    private String logs;
    // planItemId
    private String planItemId;
    
    private int diffResultCode;

    private Date dataCreateTime;
}
