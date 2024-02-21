package com.arextest.web.core.business;

import com.arextest.web.core.repository.mongo.ReportPlanStatisticRepositoryImpl;
import com.arextest.web.model.contract.contracts.QueryPlanStatisticRequestType;
import com.arextest.web.model.contract.contracts.QueryPlanStatisticResponseType;
import com.arextest.web.model.contract.contracts.QueryPlanStatisticsRequestType;
import com.arextest.web.model.contract.contracts.QueryPlanStatisticsResponseType;
import com.arextest.web.model.contract.contracts.common.CaseCount;
import com.arextest.web.model.contract.contracts.common.PlanStatistic;
import com.arextest.web.model.dto.ReportPlanStatisticDto;
import com.arextest.web.model.mapper.PlanMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class QueryPlanStatisticsService {
  private ReportPlanStatisticRepositoryImpl planStatisticRepository;
  private CaseCountService caseCountService;

  public QueryPlanStatisticsResponseType queryByApp(QueryPlanStatisticsRequestType request) {
    QueryPlanStatisticsResponseType response = new QueryPlanStatisticsResponseType();

    Pair<List<ReportPlanStatisticDto>, Long> result = planStatisticRepository.pageQueryPlanStatistic(
        request);
    List<ReportPlanStatisticDto> plans = result.getLeft();
    Long totalCount = result.getRight();
    response.setTotalCount(totalCount);

    List<String> planIds = plans.stream()
        .map(ReportPlanStatisticDto::getPlanId)
        .collect(Collectors.toList());

    Map<String, CaseCount> caseCountMap = caseCountService.calculateCaseCountsByPlanIds(planIds);
    for (ReportPlanStatisticDto plan : plans) {
      CaseCount caseCount = caseCountMap.get(plan.getPlanId());
      if (caseCount == null) {
        continue;
      }
      mergeCaseCount(plan, caseCount);
    }

    response.setPlanStatisticList(plans.stream().map(PlanMapper.INSTANCE::contractFromDto)
        .collect(Collectors.toList()));
    return response;
  }

  public QueryPlanStatisticResponseType queryOne(QueryPlanStatisticRequestType request) {
    QueryPlanStatisticResponseType response = new QueryPlanStatisticResponseType();
    ReportPlanStatisticDto plan = planStatisticRepository.findByPlanId(request.getPlanId());
    if (plan == null) {
      return response;
    }
    Map<String, CaseCount> caseCountMap = caseCountService
        .calculateCaseCountsByPlanIds(Collections.singletonList(request.getPlanId()));
    if (caseCountMap.isEmpty() || !caseCountMap.containsKey(request.getPlanId())) {
      return response;
    }
    CaseCount caseCount = caseCountMap.get(request.getPlanId());
    mergeCaseCount(plan, caseCount);
    PlanStatistic planStatistic = PlanMapper.INSTANCE.contractFromDto(plan);
    response.setPlanStatistic(planStatistic);
    return response;
  }

  /**
   * Merge case count info into plan
   * @param plan target replay plan
   * @param caseCount case count info
   */
  private void mergeCaseCount(ReportPlanStatisticDto plan, CaseCount caseCount) {
    plan.setTotalCaseCount(caseCount.getTotalCaseCount());
    plan.setErrorCaseCount(caseCount.getErrorCaseCount());
    plan.setSuccessCaseCount(caseCount.getSuccessCaseCount());
    plan.setFailCaseCount(caseCount.getFailCaseCount());
    plan.setWaitCaseCount(caseCount.getTotalCaseCount() - caseCount.getReceivedCaseCount());
    plan.setTotalOperationCount(caseCount.getTotalOperationCount());
    plan.setSuccessOperationCount(caseCount.getSuccessOperationCount());
  }
}
