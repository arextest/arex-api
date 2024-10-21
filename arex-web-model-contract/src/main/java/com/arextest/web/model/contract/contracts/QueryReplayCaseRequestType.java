package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.PagingRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QueryReplayCaseRequestType implements PagingRequest {

  private Integer pageIndex;
  private Integer pageSize;
  private Boolean needTotal;

  @NotBlank(message = "PlanItemId cannot be empty")
  private String planItemId;

  private Integer diffResultCode;

  private String keyWord;
}
