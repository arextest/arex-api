package com.arextest.web.core.repository;

import com.arextest.web.model.dto.batchcomparereport.BatchCompareReportResultDto;

import java.util.List;

/**
 * Created by rchen9 on 2023/2/9.
 */

public interface BatchCompareReportResultRepository {
    List<String> insertAll(List<BatchCompareReportResultDto> batchCompareReportResultDtoList);
}
