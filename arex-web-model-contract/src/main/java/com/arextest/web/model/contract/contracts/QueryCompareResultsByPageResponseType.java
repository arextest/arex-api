package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.PagingResponse;
import com.arextest.web.model.contract.contracts.common.CompareResult;
import lombok.Data;

import java.util.List;


@Data
public class QueryCompareResultsByPageResponseType implements PagingResponse {
    private Long totalCount;
    private List<CompareResult> compareResults;
}
