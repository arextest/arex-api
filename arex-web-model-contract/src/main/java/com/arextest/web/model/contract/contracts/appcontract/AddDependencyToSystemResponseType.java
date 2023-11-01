package com.arextest.web.model.contract.contracts.appcontract;

import lombok.Data;

@Data
public class AddDependencyToSystemResponseType {

  private String appId;
  private String operationId;
  private String dependencyId;
}
