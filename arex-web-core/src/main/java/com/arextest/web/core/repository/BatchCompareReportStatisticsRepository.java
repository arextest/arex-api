package com.arextest.web.core.repository;

import com.arextest.web.model.contract.contracts.batchcomparereport.BatchCompareSummaryItem;
import com.arextest.web.model.dto.batchcomparereport.BatchCompareReportStatisticsDto;

import java.util.List;

/**
 * Created by rchen9 on 2023/2/9.
 */
public interface BatchCompareReportStatisticsRepository extends RepositoryProvider {

    boolean updateBatchCompareReportStatistics(BatchCompareReportStatisticsDto dto);

    List<BatchCompareSummaryItem> queryBatchCompareSummary(String planId, String interfaceId);
}
