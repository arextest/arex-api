package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.DependencyWithContract;
import java.util.List;
import lombok.Data;

@Data
public class SyncResponseContractResponseType {

  private String entryPointContractStr;
  private List<DependencyWithContract> dependencyList;
}
