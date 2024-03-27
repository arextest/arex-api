package com.arextest.web.core.business;

import com.arextest.web.core.repository.mongo.ReportPlanStatisticRepositoryImpl;
import com.arextest.web.model.contract.contracts.QueryPlanStatisticRequestType;
import com.arextest.web.model.contract.contracts.QueryPlanStatisticResponseType;
import com.arextest.web.model.contract.contracts.QueryPlanStatisticsRequestType;
import com.arextest.web.model.contract.contracts.QueryPlanStatisticsResponseType;
import com.arextest.web.model.contract.contracts.common.CaseCount;
import com.arextest.web.model.contract.contracts.common.PlanStatistic;
import com.arextest.web.model.dto.ReportPlanStatisticDto;
import com.arextest.web.model.enums.ReplayStatusType;
import com.arextest.web.model.mapper.PlanMapper;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class QueryPlanStatisticsService {

  @Resource
  private ReportPlanStatisticRepositoryImpl planStatisticRepository;

  @Resource
  private CaseCountService caseCountService;

  @Resource(name = "custom-fork-join-executor")
  private ThreadPoolTaskExecutor customForkJoinExecutor;
  private static final String DEFAULT_TIMEOUT_ERR_MSG = "Replay interrupted due to timeout";

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
      checkNeedInterrupt(plan);
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
    checkNeedInterrupt(plan);
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
   *
   * @param plan      target replay plan
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

  /**
   * Interrupt abnormal plan Abnormal criteria: replay status is not finished and replay start time
   * is more than 3 hours
   */
  private void checkNeedInterrupt(ReportPlanStatisticDto plan) {
    Long startTime = Optional.ofNullable(
            Objects.equals(plan.getStatus(), ReplayStatusType.RERUNNING)
                ? plan.getLastRerunStartTime() : plan.getReplayStartTime())
        .orElse(System.currentTimeMillis());

    if (ReplayStatusType.NOT_FINISHED_STATUS.contains(plan.getStatus())
        && (System.currentTimeMillis() - startTime) > 3 * 60 * 60 * 1000) {
      plan.setStatus(ReplayStatusType.FAIL_INTERRUPTED);
      plan.setErrorMessage(DEFAULT_TIMEOUT_ERR_MSG +
          System.lineSeparator() +
          Optional.ofNullable(plan.getErrorMessage()).orElse(Strings.EMPTY));

      CompletableFuture.runAsync(() -> {
        planStatisticRepository.changePlanStatus(plan.getPlanId(),
            plan.getStatus(),
            null,
            plan.getErrorMessage(),
            null);
      }, customForkJoinExecutor);

      LOGGER.info("Plan {} is interrupted due to timeout", plan.getPlanId());
    }
  }
}
