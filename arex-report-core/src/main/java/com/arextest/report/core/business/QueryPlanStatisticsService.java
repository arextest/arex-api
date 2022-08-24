package com.arextest.report.core.business;

import com.arextest.report.core.repository.mongo.ReportPlanStatisticRepositoryImpl;
import com.arextest.report.model.api.contracts.QueryPlanStatisticsRequestType;
import com.arextest.report.model.api.contracts.QueryPlanStatisticsResponseType;
import com.arextest.report.model.api.contracts.common.CaseCount;
import com.arextest.report.model.api.contracts.common.PlanStatistic;
import com.arextest.report.model.dto.ReportPlanStatisticDto;
import com.arextest.report.model.mapper.PlanMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class QueryPlanStatisticsService {
    @Resource
    private ReportPlanStatisticRepositoryImpl planStatisticRepository;

    @Autowired
    private CaseCountService caseCountService;

    public QueryPlanStatisticsResponseType planStatistic(QueryPlanStatisticsRequestType request) {
        QueryPlanStatisticsResponseType response = new QueryPlanStatisticsResponseType();
        Pair<List<ReportPlanStatisticDto>, Long> result = planStatisticRepository.pageQueryPlanStatistic(request);

        List<String> planIds = result.getLeft().stream().map(ReportPlanStatisticDto::getPlanId)
                .collect(Collectors.toList());

        Map<String, CaseCount> caseCountMap = caseCountService.calculateCaseCountsByPlanIds(planIds);
        for (ReportPlanStatisticDto plan : result.getLeft()) {
            CaseCount caseCount = caseCountMap.get(plan.getPlanId());
            if (caseCount == null) {
                continue;
            }
            plan.setTotalCaseCount(caseCount.getTotalCaseCount());
            plan.setErrorCaseCount(caseCount.getErrorCaseCount());
            plan.setSuccessCaseCount(caseCount.getSuccessCaseCount());
            plan.setFailCaseCount(caseCount.getFailCaseCount());
            plan.setWaitCaseCount(caseCount.getTotalCaseCount() - caseCount.getReceivedCaseCount());
            plan.setTotalOperationCount(caseCount.getTotalOperationCount());
            plan.setSuccessOperationCount(caseCount.getSuccessOperationCount());
        }

        List<PlanStatistic> planStatistics = result.getLeft()
                .stream()
                .map(PlanMapper.INSTANCE::contractFromDto)
                .collect(Collectors.toList());
        response.setPlanStatisticList(planStatistics);
        response.setTotalCount(result.getRight());
        return response;
    }
}
