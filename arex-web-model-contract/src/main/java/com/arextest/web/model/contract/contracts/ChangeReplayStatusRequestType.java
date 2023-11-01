package com.arextest.web.model.contract.contracts;

import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeReplayStatusRequestType {

  @NotBlank(message = "Plan id cannot be empty")
  private String planId;
  private Integer totalCaseCount;
  private Integer status;
  private String errorMessage;
  private List<ReplayItem> items;
  private boolean rerun;

  @Data
  public static class ReplayItem {

    private String planItemId;
    private Integer totalCaseCount;
    private Integer status;
    private String errorMessage;
  }
}
