package com.arextest.web.model.dto;

import lombok.Data;

import java.util.Date;


@Data
public class ReportAppDateReplayStatisticDto {
    private String appId;
    private String date;
    
    
    private ReportPlanStatisticDto lastValidReplayInfo;
    
    private Integer replayNum;
    
    private Integer recordCaseNum;
    
    private Integer replayTotalNum;
    
    private Integer recordCaseTotalNum;

    private Date dataCreateTime;
    private Date dataUpdateTime;
}
