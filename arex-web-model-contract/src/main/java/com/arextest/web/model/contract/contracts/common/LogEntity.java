package com.arextest.web.model.contract.contracts.common;

import lombok.Data;

@Data
public class LogEntity {

  private Object baseValue;
  private Object testValue;
  private String logInfo;
  private UnmatchedPairEntity pathPair;
  private String addRefPkNodePathLeft;
  private String addRefPkNodePathRight;
  private int warn;
  private String path;
  private LogTag logTag;
}
