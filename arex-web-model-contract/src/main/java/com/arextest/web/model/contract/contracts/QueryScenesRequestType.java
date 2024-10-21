package com.arextest.web.model.contract.contracts;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QueryScenesRequestType {

  @NotBlank(message = "PlanItemId cannot be empty")
  private String planItemId;
  @NotBlank(message = "Category Name cannot be empty")
  private String categoryName;
  @NotBlank(message = "Operation name cannot be empty")
  private String operationName;
  private String differenceName;
}
