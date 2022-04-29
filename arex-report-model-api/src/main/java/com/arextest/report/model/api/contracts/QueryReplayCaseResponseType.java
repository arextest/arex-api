package com.arextest.report.model.api.contracts;

import com.arextest.report.model.api.PagingResponse;
import com.arextest.report.model.api.contracts.common.CaseDetailResult;
import lombok.Data;

import java.util.List;


@Data
public class QueryReplayCaseResponseType implements PagingResponse {

    private Long totalCount;

    private List<CaseDetailResult> result;
}
