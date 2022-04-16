package io.arex.report.model.dao.mongodb;

import io.arex.report.model.dao.mongodb.entity.ReplayAvgSummary;
import lombok.Data;


@Data
public class ReportDateStatisticCollection {
    private String date;
    
    private String userName;
    
    private ReplayAvgSummary currentReplayAvgSummary;
    
    private ReplayAvgSummary totalReplayAvgSummary;
}
