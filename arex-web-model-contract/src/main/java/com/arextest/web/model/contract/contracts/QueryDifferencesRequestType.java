package com.arextest.web.model.contract.contracts;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QueryDifferencesRequestType {

  @NotBlank(message = "PlanItemId cannot be empty")
  private String planItemId;
  @NotBlank(message = "Category Name cannot be empty")
  private String categoryName;
  @NotBlank(message = "Operation Name cannot be empty")
  private String operationName;
}
