package com.arextest.web.model.dao.mongodb;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

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
