package com.arextest.web.model.contract.contracts.replay;

import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Created by rchen9 on 2023/6/2.
 */
@Data
public class UpdateReportInfoRequestType {

  @NotBlank(message = "planId cannot be empty")
  private String planId;
  private Integer totalCaseCount;

  private List<UpdateReportItem> updateReportItems;

  @Data
  public static class UpdateReportItem {

    private String planItemId;
    private Integer totalCaseCount;
  }
}
