package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.PagingResponse;
import com.arextest.web.model.contract.contracts.common.PlanStatistic;
import lombok.Data;

import java.util.List;


@Data
public class QueryPlanStatisticsResponseType implements PagingResponse {

    private Long totalCount;
    
    private List<PlanStatistic> planStatisticList;
}
