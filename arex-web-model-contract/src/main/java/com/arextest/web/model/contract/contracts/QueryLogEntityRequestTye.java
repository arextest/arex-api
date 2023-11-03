package com.arextest.web.model.contract.contracts;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Created by rchen9 on 2023/4/12.
 */
@Data
public class QueryLogEntityRequestTye {

  @NotBlank(message = "id cannot be empty")
  private String compareResultId;
  private int logIndex;
}
