package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.DependencyWithContract;
import lombok.Data;

import java.util.List;

@Data
public class SyncResponseContractResponseType {
    private String entryPointContractStr;
    private List<DependencyWithContract> dependencyList;
}
