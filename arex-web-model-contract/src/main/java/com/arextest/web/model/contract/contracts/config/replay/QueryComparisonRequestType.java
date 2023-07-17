package com.arextest.web.model.contract.contracts.config.replay;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryComparisonRequestType {
    @NotNull(message = "appid cannot be null")
    private String appId;
    private String operationId;
//    private String dependencyId;
    private String operationName;
    private String operationType;
}
