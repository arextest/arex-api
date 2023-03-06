package com.arextest.web.core.business;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.business.iosummary.SceneReportService;
import com.arextest.web.core.business.iosummary.SummaryService;
import com.arextest.web.core.business.listener.planfinish.PlanFinishedService;
import com.arextest.web.core.repository.ReplayCompareResultRepository;
import com.arextest.web.core.repository.ReportDiffAggStatisticRepository;
import com.arextest.web.core.repository.ReportPlanItemStatisticRepository;
import com.arextest.web.core.repository.ReportPlanStatisticRepository;
import com.arextest.web.model.contract.contracts.ChangeReplayStatusRequestType;
import com.arextest.web.model.contract.contracts.PushCompareResultsRequestType;
import com.arextest.web.model.contract.contracts.common.CompareResult;
import com.arextest.web.model.dto.CompareResultDto;
import com.arextest.web.model.dto.ReportPlanStatisticDto;
import com.arextest.web.model.enums.ReplayStatusType;
import com.arextest.web.model.mapper.CompareResultMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class ReportService {
    @Resource
    private ReplayCompareResultRepository replayCompareResultRepository;
    @Resource
    private ReportPlanStatisticRepository planStatisticRepository;
    @Resource
    private ReportPlanItemStatisticRepository planItemStatisticRepository;
    @Resource
    private ReportDiffAggStatisticRepository reportDiffAggStatisticRepository;
    @Resource
    private StatisticService statisticService;
    @Resource
    private SceneService sceneService;
    @Resource
    private PlanFinishedService planFinishedService;
    @Resource
    private SummaryService summaryService;
    @Resource
    private SceneReportService sceneReportService;

    public boolean saveCompareResults(PushCompareResultsRequestType request) {
        List<CompareResultDto> results = new ArrayList<>(request.getResults().size());
        for (CompareResult cr : request.getResults()) {
            results.add(CompareResultMapper.INSTANCE.dtoFromContract(cr));
        }


        boolean success = replayCompareResultRepository.saveResults(results);
        if (!success) {
            return false;
        }
        // save caseSummary to db
        summaryService.analysis(results);

        statisticService.statisticPlanItems(results);

        sceneService.statisticScenes(results);
        return true;
    }


    public boolean changeReportStatus(ChangeReplayStatusRequestType request) {
        if (request.getStatus() == ReplayStatusType.FINISHED) {
            ReportPlanStatisticDto plan = planStatisticRepository.findByPlanId(request.getPlanId());
            int retryTimes = 3;
            boolean match = false;
            for (int i = 0; i < retryTimes; i++) {
                int count = replayCompareResultRepository.queryCompareResultCountByPlanId(request.getPlanId());
                if (!Objects.equals(count, plan.getTotalCaseCount())) {
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        LogUtils.error(LOGGER, e.getMessage(), e);
                    }
                } else {
                    match = true;
                    break;
                }
            }
            if (!match) {
                LogUtils.error(LOGGER, "The number of received cases does not match the declaration.");
            }
        }
        ReportPlanStatisticDto planDto = planStatisticRepository.changePlanStatus(request.getPlanId(),
                request.getStatus(), request.getTotalCaseCount());
        if (request.getItems() != null) {
            for (ChangeReplayStatusRequestType.ReplayItem item : request.getItems()) {
                if (Objects.equals(item.getStatus(), ReplayStatusType.FINISHED)) {
                    sceneReportService.report(request.getPlanId(), item.getPlanItemId());
                }
                planItemStatisticRepository.changePlanItemStatus(item.getPlanItemId(),
                        item.getStatus(),
                        item.getTotalCaseCount());
            }
        }
        planFinishedService.onPlanFinishEvent(planDto.getAppId(), request.getPlanId(), request.getStatus());
        return true;
    }

    public boolean deleteReport(String planId) {
        planItemStatisticRepository.deletePlanItemsByPlanId(planId);
        reportDiffAggStatisticRepository.deleteDiffAggByPlanId(planId);
        replayCompareResultRepository.deleteCompareResultsByPlanId(planId);
        return planStatisticRepository.deletePlan(planId);
    }
}
