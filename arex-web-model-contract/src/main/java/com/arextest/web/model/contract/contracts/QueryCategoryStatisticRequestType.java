package com.arextest.web.model.contract.contracts;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QueryCategoryStatisticRequestType {

  @NotBlank(message = "PlanItemId cannot be empty")
  private String planItemId;
}
