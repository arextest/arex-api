package com.arextest.report.core.business;

import com.arextest.report.core.repository.ReplayCompareResultRepository;
import com.arextest.report.core.repository.ReportPlanItemStatisticRepository;
import com.arextest.report.core.repository.ReportPlanStatisticRepository;
import com.arextest.report.model.api.contracts.ChangeReplayStatusRequestType;
import com.arextest.report.model.api.contracts.PushCompareResultsRequestType;
import com.arextest.report.model.api.contracts.common.CompareResult;
import com.arextest.report.model.dto.CompareResultDto;
import com.arextest.report.model.dto.ReportPlanStatisticDto;
import com.arextest.report.model.mapper.CompareResultMapper;
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
        return true;
    }
}
