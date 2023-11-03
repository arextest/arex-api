package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.PlanItemStatistic;
import java.util.List;
import lombok.Data;

@Data
public class QueryPlanItemStatisticsResponseType {

  private List<PlanItemStatistic> planItemStatisticList;
}
