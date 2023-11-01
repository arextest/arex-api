package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.PagingResponse;
import com.arextest.web.model.contract.contracts.common.PlanStatistic;
import java.util.List;
import lombok.Data;

@Data
public class QueryPlanStatisticsResponseType implements PagingResponse {

  private Long totalCount;

  private List<PlanStatistic> planStatisticList;
}
