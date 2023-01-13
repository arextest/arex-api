package com.arextest.web.core.business;

import com.arextest.web.core.business.config.replay.planfinish.PlanFinishedService;
import com.arextest.web.core.repository.ReplayCompareResultRepository;
import com.arextest.web.core.repository.ReportPlanItemStatisticRepository;
import com.arextest.web.core.repository.ReportPlanStatisticRepository;
import com.arextest.web.model.contract.contracts.ChangeReplayStatusRequestType;
import com.arextest.web.model.contract.contracts.PushCompareResultsRequestType;
import com.arextest.web.model.contract.contracts.common.CompareResult;
import com.arextest.web.model.dto.CompareResultDto;
import com.arextest.web.model.dto.ReportPlanStatisticDto;
import com.arextest.web.model.enums.ReplayStatusType;
import com.arextest.web.model.mapper.CompareResultMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Component
public class ReportService {
    @Resource
    private ReplayCompareResultRepository replayCompareResultRepository;
    @Resource
    private ReportPlanStatisticRepository planStatisticRepository;
    @Resource
    private ReportPlanItemStatisticRepository planItemStatisticRepository;
    @Resource
    private StatisticService statisticService;
    @Resource
    private SceneService sceneService;
    @Resource
    private PlanFinishedService planFinishedService;

    
    public boolean saveCompareResults(PushCompareResultsRequestType request) {
        List<CompareResultDto> results = new ArrayList<>(request.getResults().size());
        for (CompareResult cr : request.getResults()) {
            results.add(CompareResultMapper.INSTANCE.dtoFromContract(cr));
        }

        
        boolean success = replayCompareResultRepository.saveResults(results);
        if (!success) {
            return false;
        }

        statisticService.statisticPlanItems(results);

        sceneService.statisticScenes(results);
        return true;
    }

    
    public boolean changeReportStatus(ChangeReplayStatusRequestType request) {
        ReportPlanStatisticDto planDto = planStatisticRepository.changePlanStatus(request.getPlanId(),
                request.getStatus(), request.getTotalCaseCount());
        if (request.getItems() != null) {
            for (ChangeReplayStatusRequestType.ReplayItem item : request.getItems()) {
                planItemStatisticRepository.changePlanItemStatus(item.getPlanItemId(),
                        item.getStatus(),
                        item.getTotalCaseCount());
            }
        }
        if (request.getStatus() != null && request.getStatus().equals(ReplayStatusType.FINISHED)) {
            planFinishedService.onPlanFinishEvent(planDto.getAppId(), request.getPlanId());
        }
        return true;
    }
}
