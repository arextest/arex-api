package com.arextest.web.model.contract.contracts.config.replay;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryComparisonRequestType {
    @NotNull(message = "appid cannot be null")
    private String appId;
    private String operationId;

    /**
     * operationName and operationType are the key of dependency
     */
    private String operationName;
    private String operationType;
    private Boolean filterExpired;
}
