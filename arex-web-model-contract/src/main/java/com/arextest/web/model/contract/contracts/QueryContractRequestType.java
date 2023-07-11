package com.arextest.web.model.contract.contracts;

import lombok.Data;

@Data
public class QueryContractRequestType {
    // query entryPointContract
    private String operationId;
    // query dependency
    private String contractId;
}
