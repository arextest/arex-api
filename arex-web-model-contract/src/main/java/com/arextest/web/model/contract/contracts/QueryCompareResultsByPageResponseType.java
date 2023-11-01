package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.PagingResponse;
import com.arextest.web.model.contract.contracts.common.CompareResult;
import java.util.List;
import lombok.Data;

@Data
public class QueryCompareResultsByPageResponseType implements PagingResponse {

  private Long totalCount;
  private List<CompareResult> compareResults;
}
