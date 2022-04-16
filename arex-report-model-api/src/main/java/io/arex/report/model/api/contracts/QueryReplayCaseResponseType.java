package io.arex.report.model.api.contracts;

import io.arex.report.model.api.PagingResponse;
import io.arex.report.model.api.contracts.common.CaseDetailResult;
import lombok.Data;

import java.util.List;


@Data
public class QueryReplayCaseResponseType implements PagingResponse {

    private Long totalCount;

    private List<CaseDetailResult> result;
}
