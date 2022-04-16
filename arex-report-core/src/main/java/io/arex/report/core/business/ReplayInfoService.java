package io.arex.report.core.business;

import io.arex.report.core.repository.mongo.ReportPlanItemStatisticRepositoryImpl;
import io.arex.report.core.repository.mongo.ReportPlanStatisticRepositoryImpl;
import io.arex.report.model.api.contracts.ReportInitialRequestType;
import io.arex.report.model.dto.PlanItemDto;
import io.arex.report.model.dto.ReportPlanStatisticDto;
import io.arex.report.model.enums.ReplayStatusType;
import io.arex.report.model.mapper.PlanItemMapper;
import io.arex.report.model.mapper.PlanMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;


@Slf4j
@Component
public class ReplayInfoService {
    @Resource
    private ReportPlanStatisticRepositoryImpl reportPlanStatisticRepository;
    @Resource
    private ReportPlanItemStatisticRepositoryImpl reportPlanItemStatisticRepository;

    public boolean initPlan(ReportInitialRequestType request) {
        if (request == null) {
            return false;
        }
        try {
            ReportPlanStatisticDto reportPlanStatisticDto = PlanMapper.INSTANCE.dtoFromContract(request);
            reportPlanStatisticDto.setDataChangeCreateTime(System.currentTimeMillis());
            reportPlanStatisticDto.setReplayStartTime(System.currentTimeMillis());
            reportPlanStatisticDto.setStatus(ReplayStatusType.RUNNING);

            reportPlanStatisticRepository.findAndModifyBaseInfo(reportPlanStatisticDto);
            if (!CollectionUtils.isEmpty(request.getReportItemList())) {
                request.getReportItemList().forEach(planItem -> {
                    if (planItem == null) {
                        return;
                    }
                    PlanItemDto planItemDto = PlanItemMapper.INSTANCE.dtoFromContract(planItem);
                    planItemDto.setPlanId(request.getPlanId());
                    planItemDto.setDataCreateTime(new Date());
                    planItemDto.setStatus(ReplayStatusType.INIT);
                    reportPlanItemStatisticRepository.findAndModifyBaseInfo(planItemDto);
                });
            }
        } catch (Exception e) {
            LOGGER.error("updateReplayBaseInfo", e);
            return false;
        }
        return true;
    }
}
