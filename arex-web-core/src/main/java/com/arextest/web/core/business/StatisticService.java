package com.arextest.web.core.business;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.ReportPlanItemStatisticRepository;
import com.arextest.web.model.dto.CompareResultDto;
import com.arextest.web.model.dto.PlanItemDto;
import com.arextest.web.model.dto.ReportPlanStatisticDto;
import com.arextest.web.model.enums.DiffResultCode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StatisticService {
  @Resource
  private ReportPlanItemStatisticRepository planItemStatisticRepository;

  public void statisticPlanItems(List<CompareResultDto> results) {
    if (CollectionUtils.isEmpty(results)) {
      return;
    }
    CompareResultDto result = results.get(0);
    PlanItemDto planItem = new PlanItemDto();
    planItem.setPlanId(result.getPlanId());
    planItem.setPlanItemId(result.getPlanItemId());
    planItem.setOperationId(result.getOperationId());
    planItem.setOperationName(result.getOperationName());
    planItem.setServiceName(result.getServiceName());
    planItem.setCases(new HashMap<>());
    planItem.setErrorCases(new HashMap<>());
    planItem.setFailCases(new HashMap<>());

    for (CompareResultDto r : results) {
      inc(planItem.getCases(), r.getRecordId());
      if (r.getDiffResultCode() == DiffResultCode.COMPARED_WITHOUT_DIFFERENCE) {
        continue;
      }
      if (r.getDiffResultCode() == DiffResultCode.COMPARED_WITH_DIFFERENCE) {
        inc(planItem.getFailCases(), r.getRecordId());
      } else {
        inc(planItem.getErrorCases(), r.getRecordId());
      }
    }

    planItemStatisticRepository.updatePlanItems(planItem);
  }


  private void inc(Map<String, Integer> detail, String replayId) {
    if (!detail.containsKey(replayId)) {
      detail.put(replayId, 1);
    } else {
      detail.put(replayId, detail.get(replayId) + 1);
    }
  }
}
