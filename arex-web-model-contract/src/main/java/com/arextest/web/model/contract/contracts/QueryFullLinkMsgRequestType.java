package com.arextest.web.model.contract.contracts;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QueryFullLinkMsgRequestType {

  @NotBlank(message = "RecordId cannot be empty")
  private String recordId;
  private String planItemId;
}
