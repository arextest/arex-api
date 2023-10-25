package com.arextest.web.model.contract.contracts;

import java.util.List;

import com.arextest.web.model.contract.contracts.common.DependencyWithContract;

import lombok.Data;

@Data
public class SyncResponseContractResponseType {
    private String entryPointContractStr;
    private List<DependencyWithContract> dependencyList;
}
