package com.arextest.web.core.business;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.ReportPlanItemStatisticRepository;
import com.arextest.web.core.repository.ReportPlanStatisticRepository;
import com.arextest.web.model.dto.CompareResultDto;
import com.arextest.web.model.dto.PlanItemDto;
import com.arextest.web.model.dto.ReportPlanStatisticDto;
import com.arextest.web.model.enums.DiffResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Component
public class StatisticService {

    @Resource
    private ReportPlanItemStatisticRepository planItemStatisticRepository;

    @Resource(name = "report-statistic-executor")
    private ThreadPoolTaskExecutor executor;


    private static volatile Map<String, PlanItemDto> planItemMap = new HashMap<>();


    public void statisticPlanItems(List<CompareResultDto> results) {
        // 100000 records costs 32ms
        synchronized (planItemMap) {
            for (CompareResultDto r : results) {
                statisticCase(planItemMap, r, StatisticType.CASE_COUNT);
                if (r.getDiffResultCode() == DiffResultCode.COMPARED_WITHOUT_DIFFERENCE) {
                    continue;
                }
                if (r.getDiffResultCode() == DiffResultCode.COMPARED_WITH_DIFFERENCE) {
                    statisticCase(planItemMap, r, StatisticType.FAILED);
                } else {
                    statisticCase(planItemMap, r, StatisticType.ERROR);
                }
            }
        }
    }

    protected Map<String, PlanItemDto> getPlanItemMap() {
        return planItemMap;
    }

    private void updatePlan(Map<String, ReportPlanStatisticDto> planMap, CompareResultDto result) {
        if (!planMap.containsKey(result.getPlanId())) {
            ReportPlanStatisticDto planDto = new ReportPlanStatisticDto();
            planDto.setPlanId(result.getPlanId());
            planDto.setStatus(0);
            planMap.put(result.getPlanId(), planDto);
        }
    }

    private void statisticCase(Map<String, PlanItemDto> planItemMap, CompareResultDto result, StatisticType type) {
        PlanItemDto item = null;
        if (!planItemMap.containsKey(result.getPlanItemId())) {
            item = new PlanItemDto();
            item.setPlanId(result.getPlanId());
            item.setPlanItemId(result.getPlanItemId());
            item.setOperationId(result.getOperationId());
            item.setOperationName(result.getOperationName());
            item.setServiceName(result.getServiceName());
            item.setCases(new HashMap<>());
            item.setErrorCases(new HashMap<>());
            item.setFailCases(new HashMap<>());
            planItemMap.put(result.getPlanItemId(), item);
        } else {
            item = planItemMap.get(result.getPlanItemId());
        }
        switch (type) {
            case CASE_COUNT:
                inc(item.getCases(), result.getRecordId());
                break;
            case ERROR:
                inc(item.getErrorCases(), result.getRecordId());
                break;
            case FAILED:
                inc(item.getFailCases(), result.getRecordId());
                break;
            default:
                LogUtils.error(LOGGER, String.format("unhandled StatisticType:%s", type.toString()));
        }

    }

    private void inc(Map<String, Integer> detail, String replayId) {
        if (!detail.containsKey(replayId)) {
            detail.put(replayId, 1);
        } else {
            detail.put(replayId, detail.get(replayId) + 1);
        }
    }

    private enum StatisticType {

        CASE_COUNT,

        FAILED,

        ERROR;
    }


    public void report() {
        Map<String, PlanItemDto> tmp = null;
        synchronized (StatisticService.class) {
            long currentTimestamp = System.currentTimeMillis();
            if (MapUtils.isEmpty(planItemMap)) {
                return;
            }
            tmp = planItemMap;
            planItemMap = new HashMap<>();
            LogUtils.info(LOGGER,
                    "locking plan statistic map cost time: {} ms",
                    System.currentTimeMillis() - currentTimestamp);
        }

        CompletableFuture.completedFuture(tmp).thenApplyAsync(piMap -> {
            if (MapUtils.isEmpty(piMap)) {
                return null;
            }
            try {
                long currentTimestamp = System.currentTimeMillis();
                for (Map.Entry<String, PlanItemDto> pi : piMap.entrySet()) {
                    planItemStatisticRepository.updatePlanItems(pi.getValue());
                }
                LogUtils.info(LOGGER,
                        "updating statistic of plan items cost time: {} ms. plan items count:{}",
                        System.currentTimeMillis() - currentTimestamp,
                        piMap.size());
                piMap.clear();
            } catch (Exception e) {
                LogUtils.error(LOGGER, "updating statistics of plan items error", e);
            }
            return null;
        }, executor);
    }
}
