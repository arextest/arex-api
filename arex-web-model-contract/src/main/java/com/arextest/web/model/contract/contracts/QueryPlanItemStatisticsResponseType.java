package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.PlanItemStatistic;
import lombok.Data;

import java.util.List;


@Data
public class QueryPlanItemStatisticsResponseType {

    
    private List<PlanItemStatistic> planItemStatisticList;
}
