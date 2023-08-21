package com.arextest.web.model.contract.contracts.common;

import lombok.Data;

@Data
public class Dependency {
    private String dependencyId;
    private String operationName;
    private String operationType;
}
