package com.arextest.web.model.contract.contracts;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QueryPlanItemStatisticsRequestType {

  @NotBlank(message = "planId can not be blank")
  private String planId;

  private String planItemId;
}
