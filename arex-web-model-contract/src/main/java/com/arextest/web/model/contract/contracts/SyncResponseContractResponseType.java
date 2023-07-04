package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.Dependency;
import lombok.Data;

import java.util.Map;

@Data
public class SyncResponseContractResponseType {
    private String entryContractStr;
    // key:applicationId
    private Map<String, Dependency> dependencyMap;
}
