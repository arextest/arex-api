package com.arextest.web.model.contract.contracts;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QueryFullLinkMsgRequestType {

  @NotBlank(message = "RecordId cannot be empty")
  private String recordId;
  private String planItemId;
}
