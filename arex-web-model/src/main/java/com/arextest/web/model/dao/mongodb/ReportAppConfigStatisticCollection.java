package com.arextest.web.model.dao.mongodb;

import java.util.List;

import com.arextest.web.model.dao.mongodb.entity.ReportAppConfigSummery;

import lombok.Data;

@Data
public class ReportAppConfigStatisticCollection {
    private String userName;
    private ReportAppConfigSummery totalSummary;
    private List<ReportAppConfigSummery> groupSummery;
    private List<Integer> appIdList;

}
