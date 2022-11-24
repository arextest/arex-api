package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.PagingResponse;
import com.arextest.web.model.contract.contracts.common.CaseDetailResult;
import lombok.Data;

import java.util.List;


@Data
public class QueryReplayCaseResponseType implements PagingResponse {

    private Long totalCount;

    private List<CaseDetailResult> result;
}
