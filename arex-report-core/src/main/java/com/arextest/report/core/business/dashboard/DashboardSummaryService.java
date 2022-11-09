package com.arextest.report.core.business.dashboard;

import com.arextest.report.core.business.CaseCountService;
import com.arextest.report.core.business.config.ViewHandler;
import com.arextest.report.core.repository.ReportPlanStatisticRepository;
import com.arextest.report.model.api.contracts.DashboardAllAppDailyResultsRequestType;
import com.arextest.report.model.api.contracts.DashboardAllAppDailyResultsResponseType;
import com.arextest.report.model.api.contracts.DashboardAllAppResultsRequestType;
import com.arextest.report.model.api.contracts.DashboardAllAppResultsResponseType;
import com.arextest.report.model.api.contracts.DashboardSummaryResponseType;
import com.arextest.report.model.api.contracts.common.AppCaseDailyResult;
import com.arextest.report.model.api.contracts.common.AppCaseResult;
import com.arextest.report.model.api.contracts.common.AppDescription;
import com.arextest.report.model.api.contracts.common.CaseCount;
import com.arextest.report.model.api.contracts.config.dashboard.AppDashboardView;
import com.arextest.report.model.dto.LatestDailySuccessPlanIdDto;
import com.arextest.report.model.dto.ReportPlanStatisticDto;
import com.arextest.report.model.enums.ReplayStatusType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Component
public class DashboardSummaryService {

    private static final String DATA_CHANGE_CREATE_TIME = "dataChangeCreateTime";
    private static final String STATUS = "status";
    private static final String APP_ID = "appId";

    @Resource
    private ReportPlanStatisticRepository reportPlanStatisticRepository;

    @Resource
    private CaseCountService caseCountService;

    @Resource
    private ViewHandler<AppDashboardView> numbersDashboardViewHandler;


    public List<String> getAllAppId() {

        List<AppDashboardView> appDashboardViews = numbersDashboardViewHandler.useResultAsList();
        List<String> appIds = new ArrayList<>();
        Optional.ofNullable(appDashboardViews).orElse(Collections.emptyList()).forEach(item -> {
            appIds.add(item.getApplicationDescription().getAppId());
        });
        return appIds;
    }


    public DashboardSummaryResponseType getDashboardSummary() {
        DashboardSummaryResponseType dashboardSummaryResponseType = new DashboardSummaryResponseType();
        AppDescription appDescription = new AppDescription();

        Long replayCount = reportPlanStatisticRepository.findReplayCount();
        appDescription.setReplayCount(replayCount.intValue());

        List<AppDashboardView> appDashboardViews = Optional.ofNullable(numbersDashboardViewHandler.useResultAsList())
                .orElse(Collections.emptyList());
        appDescription.setAppCount(appDashboardViews.size());

        int sum = appDashboardViews.stream().mapToInt(AppDashboardView::getOperationCount).sum();
        appDescription.setOperationCount(sum);
        dashboardSummaryResponseType.setAppDescription(appDescription);
        return dashboardSummaryResponseType;
    }


    public DashboardSummaryResponseType getDashboardSummaryByAppId(String appId) {
        DashboardSummaryResponseType dashboardSummaryResponseType = new DashboardSummaryResponseType();
        AppDescription appDescription = new AppDescription();

        Long replayCount = reportPlanStatisticRepository.findReplayCountByAppId(appId);
        appDescription.setReplayCount(replayCount.intValue());

        AppDashboardView appDashboardView = numbersDashboardViewHandler.useResult(appId);
        appDescription.setOperationCount(appDashboardView.getOperationCount());

        appDescription.setOwner(appDashboardView.getApplicationDescription().getOwner());

        dashboardSummaryResponseType.setAppDescription(appDescription);
        return dashboardSummaryResponseType;
    }


    public DashboardAllAppResultsResponseType allAppResults(DashboardAllAppResultsRequestType request) {

        List<ReportPlanStatisticDto> latestSuccessPlanIds =
                reportPlanStatisticRepository.findLatestSuccessPlanId(DATA_CHANGE_CREATE_TIME,
                        request.getStartTime(),
                        request.getEndTime(),
                        STATUS,
                        ReplayStatusType.FINISHED,
                        APP_ID,
                        DATA_CHANGE_CREATE_TIME,
                        Boolean.TRUE);
        Map<String, AppCaseResult> resultMap = new HashMap<>();
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
        List<String> planIds = new ArrayList<>();
        latestSuccessPlanIds.forEach(item -> {
            planIds.add(item.getPlanId());
        });

        Map<String, CaseCount> caseCountMap = caseCountService.calculateCaseCountsByPlanIds(planIds);
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
        DashboardAllAppDailyResultsResponseType dashboardAllAppDailyResultsResponseType =
                new DashboardAllAppDailyResultsResponseType();

        List<MutablePair<Object, Object>> matchConditions = new ArrayList<>();
        if (StringUtils.isNotEmpty(request.getAppId())) {
            matchConditions.add(new MutablePair<>(APP_ID, request.getAppId()));
        }
        matchConditions.add(new MutablePair<>(STATUS, ReplayStatusType.FINISHED));
        List<LatestDailySuccessPlanIdDto> latestDailySuccessPlanIdDtos =
                reportPlanStatisticRepository.findLatestDailySuccessPlanId(
                        DATA_CHANGE_CREATE_TIME, request.getStartTime(), request.getEndTime(),
                        matchConditions, APP_ID, DATA_CHANGE_CREATE_TIME, DATA_CHANGE_CREATE_TIME, true);

        Map<String, AppCaseResult> resultMap = new HashMap<>();
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

        List<String> planIds = new ArrayList<>();
        latestDailySuccessPlanIdDtos.forEach(item -> {
            planIds.add(item.getPlanId());
        });
        Map<String, CaseCount> caseCountMap = caseCountService.calculateCaseCountsByPlanIds(planIds);
        resultMap.keySet().forEach(item -> {
            if (caseCountMap.containsKey(item)) {
                CaseCount caseCount = caseCountMap.get(item);
                AppCaseResult appCaseResult = resultMap.get(item);
                appCaseResult.setTotalCaseCount(caseCount.getTotalCaseCount());
                appCaseResult.setSuccessCaseCount(caseCount.getSuccessCaseCount());
                appCaseResult.setErrorCaseCount(caseCount.getErrorCaseCount());
            }
        });
        Map<String, List<AppCaseResult>> collect =
                resultMap.values().stream().collect(Collectors.groupingBy(AppCaseResult::getDate));


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
