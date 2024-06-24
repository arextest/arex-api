package com.arextest.web.core.business;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.mongo.ReportPlanItemStatisticRepositoryImpl;
import com.arextest.web.core.repository.mongo.ReportPlanStatisticRepositoryImpl;
import com.arextest.web.model.contract.contracts.ReportInitialRequestType;
import com.arextest.web.model.contract.contracts.replay.UpdateReportInfoRequestType;
import com.arextest.web.model.dto.PlanItemDto;
import com.arextest.web.model.dto.ReportPlanStatisticDto;
import com.arextest.web.model.enums.ReplayStatusType;
import com.arextest.web.model.mapper.PlanItemMapper;
import com.arextest.web.model.mapper.PlanMapper;
import com.google.common.collect.ImmutableMap;
import java.util.Date;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

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

    ImmutableMap<String, String> logTag = ImmutableMap.of("planId", request.getPlanId());
    LogUtils.info(LOGGER, logTag, "init the plan, request: {}", request);

    try {
      ReportPlanStatisticDto reportPlanStatisticDto = PlanMapper.INSTANCE.dtoFromContract(request);
      reportPlanStatisticDto.setDataChangeCreateTime(System.currentTimeMillis());
      reportPlanStatisticDto.setReplayStartTime(System.currentTimeMillis());
      reportPlanStatisticDto.setStatus(ReplayStatusType.RUNNING);

      reportPlanStatisticRepository.findAndModifyBaseInfo(reportPlanStatisticDto);
      if (CollectionUtils.isNotEmpty(request.getReportItemList())) {
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
      LogUtils.error(LOGGER, logTag, "updateReplayBaseInfo", e);
      return false;
    }
    return true;
  }

  public boolean updatePlan(UpdateReportInfoRequestType request) {
    ImmutableMap<String, String> logTag = ImmutableMap.of("planId", request.getPlanId());
    LogUtils.info(LOGGER, logTag, "update the info of plan, request: {}", request);
    try {
      ReportPlanStatisticDto reportPlanStatisticDto = PlanMapper.INSTANCE.dtoFromContract(request);
      reportPlanStatisticRepository.findAndModifyBaseInfo(reportPlanStatisticDto);
      if (CollectionUtils.isNotEmpty(request.getUpdateReportItems())) {
        request.getUpdateReportItems().forEach(updateReportItem -> {
          if (updateReportItem == null) {
            return;
          }
          PlanItemDto planItemDto = PlanItemMapper.INSTANCE.dtoFromContract(updateReportItem);
          planItemDto.setPlanId(request.getPlanId());
          reportPlanItemStatisticRepository.findAndModifyBaseInfo(planItemDto);
        });
      }
    } catch (Exception e) {
      LogUtils.error(LOGGER, logTag, "updatePlan", e);
      return false;
    }
    return true;
  }
}
