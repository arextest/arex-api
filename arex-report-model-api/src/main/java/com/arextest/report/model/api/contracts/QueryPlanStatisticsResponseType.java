package com.arextest.report.model.api.contracts;

import com.arextest.report.model.api.PagingResponse;
import com.arextest.report.model.api.contracts.common.PlanStatistic;
import lombok.Data;

import java.util.List;


@Data
public class QueryPlanStatisticsResponseType implements PagingResponse {

    private Long totalCount;
    
    private List<PlanStatistic> planStatisticList;
}
