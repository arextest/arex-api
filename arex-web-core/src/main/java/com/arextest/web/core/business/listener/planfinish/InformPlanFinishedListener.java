package com.arextest.web.core.business.listener.planfinish;

import com.arextest.web.common.HttpUtils;
import com.arextest.web.core.business.QueryPlanStatisticsService;
import com.arextest.web.core.repository.SystemConfigRepository;
import com.arextest.web.model.contract.contracts.CallbackInformRequestType;
import com.arextest.web.model.contract.contracts.QueryPlanStatisticsRequestType;
import com.arextest.web.model.contract.contracts.QueryPlanStatisticsResponseType;
import com.arextest.web.model.contract.contracts.common.PlanStatistic;
import com.arextest.web.model.contract.contracts.config.SystemConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author wildeslam.
 * @create 2023/9/25 16:33
 */
@Slf4j
@Component
public class InformPlanFinishedListener implements PlanFinishedLinstener, ApplicationContextAware {
    private static final String SUCCESS_STR = "success";
    private static final String FAIL_STR = "fail";

    @Value("${arex.front.url}")
    private String frontUrl;

    private static final String REPORT_URL_PATTERN = "%s/replay/replay/%s";

    private static QueryPlanStatisticsService queryPlanStatisticsService;
    private static SystemConfigRepository systemConfigRepository;

    @Override
    public String planFinishedAction(String appId, String planId, Integer status) {
        SystemConfig systemConfig = systemConfigRepository.getLatestSystemConfig();
        if (systemConfig == null || systemConfig.getCallbackUrl() == null) {
            return FAIL_STR;
        }

        QueryPlanStatisticsResponseType queryPlanStatisticsResponseType = queryPlan(appId, planId);
        if (queryPlanStatisticsResponseType.getTotalCount() == 0) {
            return FAIL_STR;
        }
        PlanStatistic planStatistic = queryPlanStatisticsResponseType.getPlanStatisticList().get(0);
        try {
            CallbackInformRequestType requestType = buildRequest(planStatistic);
            HttpUtils.post(systemConfig.getCallbackUrl(), requestType, Object.class);
        } catch (Throwable e) {
            LOGGER.error("callback inform failed, planId:{}", planId, e);
            return FAIL_STR;
        }
        return SUCCESS_STR;
    }

    private CallbackInformRequestType buildRequest(PlanStatistic planStatistic) {
        CallbackInformRequestType requestType = new CallbackInformRequestType();
        requestType.setCreator(planStatistic.getCreator());
        requestType.setAppId(planStatistic.getAppId());
        requestType.setAppName(planStatistic.getAppName());
        requestType.setPlanName(planStatistic.getPlanName());
        requestType.setStatus(planStatistic.getStatus());
        requestType.setTotalCaseCount(planStatistic.getTotalCaseCount());
        requestType.setErrorCaseCount(planStatistic.getErrorCaseCount());
        requestType.setFailCaseCount(planStatistic.getFailCaseCount());
        requestType.setSuccessCaseCount(planStatistic.getSuccessCaseCount());
        requestType.setWaitCaseCount(planStatistic.getWaitCaseCount());
        requestType.setElapsedMillSeconds(planStatistic.getReplayEndTime() - planStatistic.getReplayStartTime());
        if (planStatistic.getTotalCaseCount() != 0 && planStatistic.getSuccessCaseCount() != null
        && planStatistic.getTotalCaseCount() != null) {
            requestType.setPassRate(planStatistic.getSuccessCaseCount().doubleValue() / planStatistic.getTotalCaseCount());
        }
        if (frontUrl != null) {
            requestType.setReportUrl(String.format(REPORT_URL_PATTERN, frontUrl, requestType.getAppId()));
        }
        return requestType;
    }

    private QueryPlanStatisticsResponseType queryPlan(String appId, String planId) {
        QueryPlanStatisticsRequestType request = new QueryPlanStatisticsRequestType();
        request.setAppId(appId);
        request.setNeedTotal(true);
        request.setPageSize(1);
        request.setPageIndex(1);
        request.setPlanId(planId);
        return queryPlanStatisticsService.planStatistic(request);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        queryPlanStatisticsService = (QueryPlanStatisticsService) applicationContext.getBean(
            "queryPlanStatisticsService");
        systemConfigRepository = (SystemConfigRepository) applicationContext.getBean(
            "systemConfigRepositoryImpl");
    }
}
