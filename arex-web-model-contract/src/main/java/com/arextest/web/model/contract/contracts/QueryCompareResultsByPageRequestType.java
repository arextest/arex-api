package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.PagingRequest;
import lombok.Data;

@Data
public class QueryCompareResultsByPageRequestType implements PagingRequest {

  private Integer pageIndex;
  private Integer pageSize;
  private Boolean needTotal;

  private String planId;
}
