package com.arextest.web.model.contract.contracts;

import java.util.List;

import com.arextest.web.model.contract.contracts.common.PlanItemStatistic;

import lombok.Data;

@Data
public class QueryPlanItemStatisticsResponseType {

    private List<PlanItemStatistic> planItemStatisticList;
}
