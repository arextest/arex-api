package io.arex.report.core.repository;

import io.arex.report.model.api.contracts.QueryPlanStatisticsRequestType;
import io.arex.report.model.dto.LatestDailySuccessPlanIdDto;
import io.arex.report.model.dto.ReportPlanStatisticDto;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Date;
import java.util.List;


public interface ReportPlanStatisticRepository extends RepositoryProvider {
    List<ReportPlanStatisticDto> findByDataCreateTimeBetween(Date startTime, Date endTime);

    ReportPlanStatisticDto findByPlanId(Long planId);

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

    ReportPlanStatisticDto changePlanStatus(Long planId, Integer status, Integer totalCaseCount);
}
