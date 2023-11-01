package com.arextest.web.model.contract.contracts.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppDescription {

  private String appId;

  private String owner;

  private Integer appCount;

  private Integer operationCount;

  private Integer replayCount;
}
