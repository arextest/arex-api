package com.arextest.web.model.dao.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document(collection = "ReportAppDateReplayStatistic")
public class ReportAppDateReplayStatisticCollection {
    private String appId;
    private String date;
    
    
    private ReportPlanStatisticCollection lastValidReplayInfo;
    
    private Integer replayNum;
    
    private Integer recordCaseNum;
    
    private Integer replayTotalNum;
    
    private Integer recordCaseTotalNum;
}
