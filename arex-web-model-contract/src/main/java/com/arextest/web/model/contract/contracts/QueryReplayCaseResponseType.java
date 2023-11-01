package com.arextest.web.model.contract.contracts;

import java.util.List;

import com.arextest.web.model.contract.PagingResponse;
import com.arextest.web.model.contract.contracts.common.CaseDetailResult;

import lombok.Data;

@Data
public class QueryReplayCaseResponseType implements PagingResponse {

    private Long totalCount;

    private List<CaseDetailResult> result;
}
