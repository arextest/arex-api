package com.arextest.web.core.repository;

import com.arextest.web.model.contract.contracts.QueryPlanStatisticsRequestType;
import com.arextest.web.model.dto.LatestDailySuccessPlanIdDto;
import com.arextest.web.model.dto.ReportPlanStatisticDto;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Date;
import java.util.List;


public interface ReportPlanStatisticRepository extends RepositoryProvider {
    List<ReportPlanStatisticDto> findByDataCreateTimeBetween(Date startTime, Date endTime);

    ReportPlanStatisticDto findByPlanId(String planId);

    Pair<List<ReportPlanStatisticDto>, Long> pageQueryPlanStatistic(QueryPlanStatisticsRequestType request);

    boolean findAndModifyBaseInfo(ReportPlanStatisticDto result);

    Long findReplayCount();

    Long findReplayCountByAppId(String appId);

    List<ReportPlanStatisticDto> findLatestSuccessPlanId(String rangeField, Long startTime, Long endTime,
                                                         String matchField, Integer matchValue,
                                                         String groupField, String orderField, boolean desc);

    List<LatestDailySuccessPlanIdDto> findLatestDailySuccessPlanId(String rangeField, Long startTime, Long endTime,
                                                                   List<MutablePair<Object, Object>> matches, String groupField,
                                                                   String timeDate, String orderField, boolean desc);

    ReportPlanStatisticDto changePlanStatus(String planId, Integer status, Integer totalCaseCount, String errorMessage);

    boolean deletePlan(String planId);
}
