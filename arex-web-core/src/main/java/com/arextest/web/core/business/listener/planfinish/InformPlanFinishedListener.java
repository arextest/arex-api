package com.arextest.web.core.business.listener.planfinish;

import com.arextest.web.common.HttpUtils;
import com.arextest.web.core.repository.ReportPlanStatisticRepository;
import com.arextest.web.core.repository.SystemConfigRepository;
import com.arextest.web.model.contract.contracts.CallbackInformRequestType;
import com.arextest.web.model.dto.ReportPlanStatisticDto;
import com.arextest.web.model.contract.contracts.config.SystemConfig;
import com.arextest.web.model.enums.SystemConfigTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wildeslam.
 * @create 2023/9/25 16:33
 */
@Slf4j
@Component
public class InformPlanFinishedListener implements PlanFinishedLinstener {
    @Autowired
    private ReportPlanStatisticRepository reportPlanStatisticRepository;
    @Autowired
    private SystemConfigRepository systemConfigRepository;

    private static final String SUCCESS_STR = "success";
    private static final String FAIL_STR = "fail";


    @Override
    public String planFinishedAction(String appId, String planId, Integer status) {
        SystemConfig systemConfig =
            systemConfigRepository.queryByType(SystemConfigTypeEnum.CALLBACK_INFORM.getCode());
        if (systemConfig == null || systemConfig.getCallbackUrl() == null) {
            return FAIL_STR;
        }
        ReportPlanStatisticDto reportPlanStatisticDto = reportPlanStatisticRepository.findByPlanId(planId);
        try {
            CallbackInformRequestType requestType = buildRequest(reportPlanStatisticDto);
            HttpUtils.post(systemConfig.getCallbackUrl(), requestType, Object.class);
        } catch (Throwable e) {
            LOGGER.error("callback inform failed, planId:{}", planId, e);
            return FAIL_STR;
        }
        return SUCCESS_STR;
    }

    CallbackInformRequestType buildRequest(ReportPlanStatisticDto reportPlanStatisticDto) {
        CallbackInformRequestType requestType = new CallbackInformRequestType();
        requestType.setCreator(reportPlanStatisticDto.getCreator());
        requestType.setAppId(reportPlanStatisticDto.getAppId());
        requestType.setAppName(reportPlanStatisticDto.getAppName());
        requestType.setPlanName(reportPlanStatisticDto.getPlanName());
        requestType.setStatus(reportPlanStatisticDto.getStatus());
        requestType.setTotalCaseCount(reportPlanStatisticDto.getTotalCaseCount());
        requestType.setErrorCaseCount(reportPlanStatisticDto.getErrorCaseCount());
        requestType.setFailCaseCount(reportPlanStatisticDto.getFailCaseCount());
        requestType.setSuccessCaseCount(reportPlanStatisticDto.getSuccessCaseCount());
        requestType.setWaitCaseCount(reportPlanStatisticDto.getWaitCaseCount());
        requestType.setElapsedMillSeconds(reportPlanStatisticDto.getReplayEndTime() - reportPlanStatisticDto.getReplayStartTime());
        if (reportPlanStatisticDto.getTotalCaseCount() != 0 && reportPlanStatisticDto.getSuccessCaseCount() != null
        && reportPlanStatisticDto.getTotalCaseCount() != null) {
            requestType.setPassRate(reportPlanStatisticDto.getSuccessCaseCount().doubleValue() / reportPlanStatisticDto.getTotalCaseCount());
        }
        return requestType;
    }

}
