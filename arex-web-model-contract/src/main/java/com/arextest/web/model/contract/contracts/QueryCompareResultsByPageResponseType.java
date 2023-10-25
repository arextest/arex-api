package com.arextest.web.model.contract.contracts;

import java.util.List;

import com.arextest.web.model.contract.PagingResponse;
import com.arextest.web.model.contract.contracts.common.CompareResult;

import lombok.Data;

@Data
public class QueryCompareResultsByPageResponseType implements PagingResponse {
    private Long totalCount;
    private List<CompareResult> compareResults;
}
