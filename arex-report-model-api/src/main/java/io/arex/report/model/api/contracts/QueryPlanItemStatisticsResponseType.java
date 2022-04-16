package io.arex.report.model.api.contracts;

import io.arex.report.model.api.contracts.common.PlanItemStatistic;
import lombok.Data;

import java.util.List;


@Data
public class QueryPlanItemStatisticsResponseType {

    
    private List<PlanItemStatistic> planItemStatisticList;
}
