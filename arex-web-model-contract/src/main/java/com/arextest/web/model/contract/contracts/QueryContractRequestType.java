package com.arextest.web.model.contract.contracts;

import lombok.Data;

@Data
public class QueryContractRequestType {
    //query global contract
    private String appId;
    // query entryPointContract
    private String operationId;
    // query dependency
    private String contractId;
    private Integer contractType;
}
