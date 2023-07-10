package com.arextest.web.model.contract.contracts;

import lombok.Data;

@Data
public class OverwriteContractRequestType {
    private String contractId;

    private String operationId;

    private String operationResponse;
}
