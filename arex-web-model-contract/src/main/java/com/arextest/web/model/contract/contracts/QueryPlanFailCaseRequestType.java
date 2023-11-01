package com.arextest.web.model.contract.contracts;

import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Created by rchen9 on 2023/5/7.
 */
@Data
public class QueryPlanFailCaseRequestType {

  @NotBlank(message = "planId cannot be empty")
  private String planId;
  private List<String> planItemIdList;
  private List<String> recordIdList;
  private List<Integer> diffResultCodeList;
}
