package io.arex.report.model.api.contracts;

import io.arex.report.model.api.PagingResponse;
import io.arex.report.model.api.contracts.common.CompareResult;
import lombok.Data;

import java.util.List;


@Data
public class QueryCompareResultsByPageResponseType implements PagingResponse {
    private Long totalCount;
    private List<CompareResult> compareResults;
}
