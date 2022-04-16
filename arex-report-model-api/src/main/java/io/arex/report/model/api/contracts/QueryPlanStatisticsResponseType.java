package io.arex.report.model.api.contracts;

import io.arex.report.model.api.PagingResponse;
import io.arex.report.model.api.contracts.common.PlanStatistic;
import lombok.Data;

import java.util.List;


@Data
public class QueryPlanStatisticsResponseType implements PagingResponse {

    private Long totalCount;
    
    private List<PlanStatistic> planStatisticList;
}
