package io.arex.report.core.business.dashboard;

import io.arex.report.common.HttpUtils;
import io.arex.report.core.business.CaseCountService;
import io.arex.report.core.business.config.ApplicationProperties;
import io.arex.report.core.repository.ReportPlanItemStatisticRepository;
import io.arex.report.core.repository.ReportPlanStatisticRepository;
import io.arex.report.model.api.contracts.*;
import io.arex.report.model.api.contracts.common.AppCaseDailyResult;
import io.arex.report.model.api.contracts.common.AppCaseResult;
import io.arex.report.model.api.contracts.common.AppDescription;
import io.arex.report.model.api.contracts.common.CaseCount;
import io.arex.report.model.dto.LatestDailySuccessPlanIdDto;
import io.arex.report.model.dto.ReportPlanStatisticDto;
import io.arex.report.model.enums.ReplayStatusType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Component
public class DashboardSummaryService {

    private static final String DATA_CHANGE_CREATE_TIME = "dataChangeCreateTime";
    private static final String STATUS = "status";
    private static final String APP_ID = "appId";
    private static final String TOTAL_APP_DESCRIPTION = "/config/dashboard/";
    private static final String SINGLE_APP_DESCRIPTION = "/config/dashboard/appId/";
    private static final String BODY = "body";
    private static final String OPERATION_COUNT = "operationCount";
    private static final String OWNER = "owner";
    private static final String APPLICATION_DESCRIPTION = "applicationDescription";

    @Resource
    private ReportPlanStatisticRepository reportPlanStatisticRepository;

    @Resource
    private ReportPlanItemStatisticRepository reportPlanItemStatisticRepository;

    @Resource
    private CaseCountService caseCountService;

    @Resource
    private ApplicationProperties applicationProperties;

    
    public List<String> getAllAppId() {
        
        ResponseEntity<String> responseEntity = HttpUtils.get(applicationProperties.getConfigServiceUrl() + TOTAL_APP_DESCRIPTION, String.class);
        List<String> appIds = new ArrayList<>();
        try {
            JSONObject entityBody = new JSONObject(responseEntity.getBody());
            JSONArray bodyJSONArray = entityBody.getJSONArray(BODY);
            for (int i = 0; i < bodyJSONArray.length(); i++) {
                JSONObject jsonObject = bodyJSONArray.getJSONObject(i);
                JSONObject applicationDescription = jsonObject.getJSONObject(APPLICATION_DESCRIPTION);
                appIds.add(applicationDescription.getString(APP_ID));
            }
        } catch (JSONException e) {
            LOGGER.error("getAllAppId", e);
        }
        return appIds;
    }

    
    public DashboardSummaryResponseType getDashboardSummary() {
        DashboardSummaryResponseType dashboardSummaryResponseType = new DashboardSummaryResponseType();
        AppDescription appDescription = new AppDescription();
        Long replayCount = reportPlanStatisticRepository.findReplayCount();
        
        appDescription.setReplayCount(replayCount.intValue());
        
        ResponseEntity<String> responseEntity = HttpUtils.get(applicationProperties.getConfigServiceUrl() + TOTAL_APP_DESCRIPTION, String.class);
        try {
            JSONObject entityBody = new JSONObject(responseEntity.getBody());
            JSONArray bodyJSONArray = entityBody.getJSONArray(BODY);
            
            appDescription.setAppCount(bodyJSONArray.length());
            
            int operationCount = 0;
            for (int i = 0; i < bodyJSONArray.length(); i++) {
                JSONObject jsonObject = bodyJSONArray.getJSONObject(i);
                operationCount = operationCount + (Integer) jsonObject.get(OPERATION_COUNT);
            }
            appDescription.setOperationCount(operationCount);
            dashboardSummaryResponseType.setAppDescription(appDescription);
            return dashboardSummaryResponseType;

        } catch (JSONException e) {
            LOGGER.error("getDashboardSummary", e);
        }
        return null;
    }

    
    public DashboardSummaryResponseType getDashboardSummaryByAppId(String appId) {
        DashboardSummaryResponseType dashboardSummaryResponseType = new DashboardSummaryResponseType();
        AppDescription appDescription = new AppDescription();
        
        Long replayCount = reportPlanStatisticRepository.findReplayCountByAppId(appId);
        
        appDescription.setReplayCount(replayCount.intValue());
        
        String url = applicationProperties.getConfigServiceUrl() + SINGLE_APP_DESCRIPTION + appId;
        ResponseEntity<String> responseEntity = HttpUtils.get(url, String.class);
        try {
            JSONObject entityBody = new JSONObject(responseEntity.getBody());
            JSONObject jsonObject = entityBody.getJSONObject(BODY);
            
            appDescription.setOperationCount((Integer) jsonObject.get(OPERATION_COUNT));
            
            appDescription.setOwner(jsonObject.getJSONObject(APPLICATION_DESCRIPTION).getString(OWNER));
            dashboardSummaryResponseType.setAppDescription(appDescription);
            return dashboardSummaryResponseType;
        } catch (JSONException e) {
            LOGGER.error("getDashboardSummaryByAppId", e);
        }
        return null;
    }


    
    public DashboardAllAppResultsResponseType allAppResults(DashboardAllAppResultsRequestType request) {

        List<ReportPlanStatisticDto> latestSuccessPlanIds = reportPlanStatisticRepository.findLatestSuccessPlanId(DATA_CHANGE_CREATE_TIME,
                request.getStartTime(), request.getEndTime(), STATUS, ReplayStatusType.FINISHED, APP_ID, DATA_CHANGE_CREATE_TIME, Boolean.TRUE);
        Map<Long, AppCaseResult> resultMap = new HashMap<>();
        latestSuccessPlanIds.forEach(item -> {
            AppCaseResult appCaseResult = new AppCaseResult();
            appCaseResult.setAppId(item.getAppId());
            appCaseResult.setAppName(item.getAppName());
            appCaseResult.setCreateTime(item.getDataChangeCreateTime());
            appCaseResult.setTotalCaseCount(0);
            appCaseResult.setSuccessCaseCount(0);
            appCaseResult.setErrorCaseCount(0);
            resultMap.put(item.getPlanId(), appCaseResult);
        });
        List<Long> planIds = new ArrayList<>();
        latestSuccessPlanIds.forEach(item -> {
            planIds.add(item.getPlanId());
        });

        Map<Long, CaseCount> caseCountMap = caseCountService.calculateCaseCountsByPlanIds(planIds);
        resultMap.keySet().forEach(item -> {
            if (caseCountMap.containsKey(item)) {
                CaseCount caseCount = caseCountMap.get(item);
                AppCaseResult appCaseResult = resultMap.get(item);
                appCaseResult.setTotalCaseCount(caseCount.getTotalCaseCount());
                appCaseResult.setSuccessCaseCount(caseCount.getSuccessCaseCount());
                appCaseResult.setErrorCaseCount(caseCount.getErrorCaseCount());
            }
        });
        List<AppCaseResult> collect = new ArrayList<>(resultMap.values());
        DashboardAllAppResultsResponseType result = new DashboardAllAppResultsResponseType();
        result.setCaseResults(collect);
        return result;
    }

    
    public DashboardAllAppDailyResultsResponseType allAppDailyResults(DashboardAllAppDailyResultsRequestType request) {
        DashboardAllAppDailyResultsResponseType dashboardAllAppDailyResultsResponseType = new DashboardAllAppDailyResultsResponseType();

        List<MutablePair<Object, Object>> matchConditions = new ArrayList<>();
        if (StringUtils.isNotEmpty(request.getAppId())) {
            matchConditions.add(new MutablePair<>(APP_ID, request.getAppId()));
        }
        matchConditions.add(new MutablePair<>(STATUS, ReplayStatusType.FINISHED));
        List<LatestDailySuccessPlanIdDto> latestDailySuccessPlanIdDtos = reportPlanStatisticRepository.findLatestDailySuccessPlanId(
                DATA_CHANGE_CREATE_TIME, request.getStartTime(), request.getEndTime(),
                matchConditions, APP_ID, DATA_CHANGE_CREATE_TIME, DATA_CHANGE_CREATE_TIME, true);

        Map<Long, AppCaseResult> resultMap = new HashMap<>();
        latestDailySuccessPlanIdDtos.forEach(item -> {
            AppCaseResult appCaseResult = new AppCaseResult();
            appCaseResult.setAppId(item.getAppId());
            appCaseResult.setCreateTime(item.getDataChangeCreateTime());
            appCaseResult.setDate(item.getDateTime());
            appCaseResult.setTotalCaseCount(0);
            appCaseResult.setSuccessCaseCount(0);
            appCaseResult.setErrorCaseCount(0);
            resultMap.put(item.getPlanId(), appCaseResult);
        });

        List<Long> planIds = new ArrayList<>();
        latestDailySuccessPlanIdDtos.forEach(item -> {
            planIds.add(item.getPlanId());
        });
        Map<Long, CaseCount> caseCountMap = caseCountService.calculateCaseCountsByPlanIds(planIds);
        resultMap.keySet().forEach(item -> {
            if (caseCountMap.containsKey(item)) {
                CaseCount caseCount = caseCountMap.get(item);
                AppCaseResult appCaseResult = resultMap.get(item);
                appCaseResult.setTotalCaseCount(caseCount.getTotalCaseCount());
                appCaseResult.setSuccessCaseCount(caseCount.getSuccessCaseCount());
                appCaseResult.setErrorCaseCount(caseCount.getErrorCaseCount());
            }
        });
        Map<String, List<AppCaseResult>> collect = resultMap.values().stream().collect(Collectors.groupingBy(AppCaseResult::getDate));

        
        List<String> betweenDate = getBetweenDate(request.getStartTime(), request.getEndTime());
        List<AppCaseDailyResult> appCaseDailyResults = new ArrayList<>();
        betweenDate.forEach(item -> {
            AppCaseDailyResult appCaseDailyResult = new AppCaseDailyResult();
            appCaseDailyResult.setDate(item);
            appCaseDailyResult.setCaseResults(collect.getOrDefault(item, null));
            appCaseDailyResults.add(appCaseDailyResult);
        });
        dashboardAllAppDailyResultsResponseType.setCaseResults(appCaseDailyResults);
        return dashboardAllAppDailyResultsResponseType;
    }

    private List<String> getBetweenDate(Long start, Long end) {
        List<String> list = new ArrayList<>();

        
        LocalDate startDate = Instant.ofEpochMilli(start).atZone(ZoneOffset.ofHours(8)).toLocalDate();
        LocalDate endDate = Instant.ofEpochMilli(end).atZone(ZoneOffset.ofHours(8)).toLocalDate();
        long distance = ChronoUnit.DAYS.between(startDate, endDate);
        if (distance < 0) {
            return list;
        }
        Stream.iterate(startDate, d -> d.plusDays(1)).limit(distance + 1).forEach(f -> list.add(f.toString()));
        return list;
    }

}
