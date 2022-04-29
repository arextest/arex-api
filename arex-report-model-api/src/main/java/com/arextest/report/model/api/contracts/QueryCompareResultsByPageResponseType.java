package com.arextest.report.model.api.contracts;

import com.arextest.report.model.api.PagingResponse;
import com.arextest.report.model.api.contracts.common.CompareResult;
import lombok.Data;

import java.util.List;


@Data
public class QueryCompareResultsByPageResponseType implements PagingResponse {
    private Long totalCount;
    private List<CompareResult> compareResults;
}
