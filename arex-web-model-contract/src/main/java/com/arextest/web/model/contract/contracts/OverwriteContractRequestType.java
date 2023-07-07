package com.arextest.web.model.contract.contracts;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OverwriteContractRequestType {
    @NotNull(message = "contractId cannot be empty")
    private String id;

    private String operationResponse;
}
