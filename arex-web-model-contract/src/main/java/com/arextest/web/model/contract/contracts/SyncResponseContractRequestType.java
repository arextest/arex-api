package com.arextest.web.model.contract.contracts;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SyncResponseContractRequestType {
    private String appId;
    @NotNull(message = "operationId cannot be empty")
    private String operationId;
}
