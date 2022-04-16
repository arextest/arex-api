package io.arex.report.model.dao.mongodb.entity;

import lombok.Data;


@Data
public class ReplayAvgSummary {
    
    private int replayNum;
    
    private int replayCaseNumAvg;
    
    private float replayPassRateAvg;
    
    private long replayElapsedAvg;
    
    private int recordCaseNum;
}
