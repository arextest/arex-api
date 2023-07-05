package com.arextest.web.model.contract.contracts.config.replay;

import lombok.Data;

@Data
public class QueryComparisonRequestType {
    private String appId;
    private String operationId;
    private String dependencyId;
}
