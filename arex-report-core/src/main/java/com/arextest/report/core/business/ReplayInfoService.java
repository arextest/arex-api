package com.arextest.report.core.business;

import com.arextest.report.core.repository.mongo.ReportPlanItemStatisticRepositoryImpl;
import com.arextest.report.core.repository.mongo.ReportPlanStatisticRepositoryImpl;
import com.arextest.report.model.api.contracts.ReportInitialRequestType;
import com.arextest.report.model.dto.PlanItemDto;
import com.arextest.report.model.dto.ReportPlanStatisticDto;
import com.arextest.report.model.enums.ReplayStatusType;
import com.arextest.report.model.mapper.PlanItemMapper;
import com.arextest.report.model.mapper.PlanMapper;
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
