package com.arextest.web.model.contract.contracts;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QueryReplayMsgRequestType {

  @NotBlank(message = "Replay Id cannot be empty")
  private String id;
}
