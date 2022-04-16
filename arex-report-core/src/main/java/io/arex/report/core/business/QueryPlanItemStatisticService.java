package io.arex.report.core.business;

import io.arex.report.core.repository.mongo.ReportPlanItemStatisticRepositoryImpl;
import io.arex.report.core.repository.mongo.ReportPlanStatisticRepositoryImpl;
import io.arex.report.model.api.contracts.QueryPlanItemStatisticsRequestType;
import io.arex.report.model.api.contracts.QueryPlanItemStatisticsResponseType;
import io.arex.report.model.api.contracts.common.PlanItemStatistic;
import io.arex.report.model.dto.PlanItemDto;
import io.arex.report.model.dto.ReportPlanStatisticDto;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class QueryPlanItemStatisticService {
    @Resource
    private ReportPlanStatisticRepositoryImpl reportPlanStatisticRepository;
    @Resource
    private ReportPlanItemStatisticRepositoryImpl reportPlanItemStatisticRepository;

    public QueryPlanItemStatisticsResponseType planItemStatistic(QueryPlanItemStatisticsRequestType request) {
        QueryPlanItemStatisticsResponseType response = new QueryPlanItemStatisticsResponseType();
        if (request == null || (request.getPlanId() == null && request.getPlanItemId() == null)) {
            return response;
        }
        List<PlanItemDto> planItemDtoList =
                reportPlanItemStatisticRepository.findByPlanIdAndPlanItemId(request.getPlanId(),
                        request.getPlanItemId());
        if (CollectionUtils.isEmpty(planItemDtoList)) {
            return response;
        }
        List<PlanItemStatistic> result = planItemDtoList.stream().map(this::covert)
                .sorted(Comparator.comparing(PlanItemStatistic::getFailCaseCount, Comparator.reverseOrder())
                        .thenComparing(PlanItemStatistic::getErrorCaseCount, Comparator.reverseOrder())
                        .thenComparing(PlanItemStatistic::getSuccessCaseCount, Comparator.reverseOrder()))
                .collect(Collectors.toList());
        response.setPlanItemStatisticList(result);
        return response;
    }

    private PlanItemStatistic covert(PlanItemDto dto) {
        PlanItemStatistic planItemStatistic = new PlanItemStatistic();
        if (dto == null) {
            return planItemStatistic;
        }
        planItemStatistic.setPlanItemId(dto.getPlanItemId());
        planItemStatistic.setPlanId(dto.getPlanId());
        planItemStatistic.setOperationId(dto.getOperationId());
        planItemStatistic.setOperationName(dto.getOperationName());
        planItemStatistic.setServiceName(dto.getServiceName());
        planItemStatistic.setStatus(dto.getStatus());
        planItemStatistic.setReplayStartTime(dto.getReplayStartTime());
        planItemStatistic.setReplayEndTime(dto.getReplayEndTime());

        Map<String, Integer> failedCaseMap = dto.getFailCases() == null ? new HashMap<>() : dto.getFailCases();
        Map<String, Integer> errorCaseMap = dto.getErrorCases() == null ? new HashMap<>() : dto.getErrorCases();
        MapDifference<String, Integer> difference = Maps.difference(failedCaseMap, errorCaseMap);

        int totalCaseCount = dto.getTotalCaseCount() == null ? 0 : dto.getTotalCaseCount();
        int receivedCaseCount = dto.getCases() == null ? 0 : dto.getCases().size();
        int failedCaseCount = difference.entriesOnlyOnLeft().size();
        int errorCaseCount = dto.getErrorCases() == null ? 0 : dto.getErrorCases().size();
        planItemStatistic.setTotalCaseCount(totalCaseCount);
        planItemStatistic.setSuccessCaseCount(receivedCaseCount - failedCaseCount - errorCaseCount);
        planItemStatistic.setFailCaseCount(failedCaseCount);
        planItemStatistic.setErrorCaseCount(errorCaseCount);
        planItemStatistic.setWaitCaseCount(totalCaseCount - receivedCaseCount);
        planItemStatistic.setStatus(dto.getStatus());

        
        if (dto.getPlanId() != null) {
            ReportPlanStatisticDto planStatisticDto = reportPlanStatisticRepository.findByPlanId(dto.getPlanId());
            planItemStatistic.setAppId(planStatisticDto.getAppId());
            planItemStatistic.setCaseSourceType(planStatisticDto.getCaseSourceType());
            planItemStatistic.setCaseStartTime(planStatisticDto.getCaseStartTime());
            planItemStatistic.setCaseEndTime(planStatisticDto.getCaseEndTime());
            planItemStatistic.setSourceEnv(planStatisticDto.getSourceEnv());
            planItemStatistic.setTargetEnv(planStatisticDto.getTargetEnv());
            planItemStatistic.setSourceHost(planStatisticDto.getSourceHost());
            planItemStatistic.setTargetHost(planStatisticDto.getTargetHost());
        }
        return planItemStatistic;
    }
}
