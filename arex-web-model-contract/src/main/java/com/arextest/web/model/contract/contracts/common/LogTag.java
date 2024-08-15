package com.arextest.web.model.contract.contracts.common;

import lombok.Data;

@Data
public class LogTag {

  private int errorType;

  private NodeErrorType nodeErrorType;


  @Data
  public static class NodeErrorType {

    private String baseNodeType;
    private String testNodeType;
  }
}
