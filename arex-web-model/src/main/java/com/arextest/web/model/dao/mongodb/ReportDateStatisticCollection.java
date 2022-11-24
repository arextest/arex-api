package com.arextest.web.model.dao.mongodb;

import com.arextest.web.model.dao.mongodb.entity.ReplayAvgSummary;
import lombok.Data;


@Data
public class ReportDateStatisticCollection {
    private String date;
    
    private String userName;
    
    private ReplayAvgSummary currentReplayAvgSummary;
    
    private ReplayAvgSummary totalReplayAvgSummary;
}
