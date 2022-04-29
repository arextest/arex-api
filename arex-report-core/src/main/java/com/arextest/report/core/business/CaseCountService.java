package com.arextest.report.core.business;

import com.arextest.report.core.repository.ReportPlanItemStatisticRepository;
import com.arextest.report.model.api.contracts.common.CaseCount;
import com.arextest.report.model.dto.PlanItemDto;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class CaseCountService {

    @Resource
    private ReportPlanItemStatisticRepository planItemStatisticRepository;

    public Map<Long, CaseCount> calculateCaseCountsByPlanIds(List<Long> planIds) {
        Map<Long, CaseCount> caseCountMap = new HashMap<>();
        List<PlanItemDto> planItems = planItemStatisticRepository.findByPlanIds(planIds);
        for (PlanItemDto planItem : planItems) {
            if (!caseCountMap.containsKey(planItem.getPlanId())) {
                caseCountMap.put(planItem.getPlanId(), new CaseCount());
            }
            CaseCount caseCount = caseCountMap.get(planItem.getPlanId());

            Map<String, Integer> failedCaseMap =
                    planItem.getFailCases() == null ? new HashMap<>() : planItem.getFailCases();
            Map<String, Integer> errorCaseMap =
                    planItem.getErrorCases() == null ? new HashMap<>() : planItem.getErrorCases();
            MapDifference<String, Integer> difference = Maps.difference(failedCaseMap, errorCaseMap);

            int errorCaseCount = errorCaseMap.size();
            int failedCaseCount = difference.entriesOnlyOnLeft().size();
            int totalCaseCount = planItem.getTotalCaseCount() == null ? 0 : planItem.getTotalCaseCount();
            int receivedCaseCount = planItem.getCases() == null ? 0 : planItem.getCases().size();

            caseCount.setTotalCaseCount(caseCount.getTotalCaseCount() + totalCaseCount);
            caseCount.setReceivedCaseCount(caseCount.getReceivedCaseCount() + receivedCaseCount);
            caseCount.setErrorCaseCount(caseCount.getErrorCaseCount() + errorCaseCount);
            caseCount.setFailCaseCount(caseCount.getFailCaseCount() + failedCaseCount);
            caseCount.setSuccessCaseCount(caseCount.getSuccessCaseCount() + receivedCaseCount
                    - errorCaseCount
                    - failedCaseCount);

            caseCount.setTotalOperationCount(caseCount.getTotalOperationCount() + 1);
            if (errorCaseCount == 0 && failedCaseCount == 0) {
                caseCount.setSuccessOperationCount(caseCount.getSuccessOperationCount() + 1);
            }
        }
        return caseCountMap;
    }
}
