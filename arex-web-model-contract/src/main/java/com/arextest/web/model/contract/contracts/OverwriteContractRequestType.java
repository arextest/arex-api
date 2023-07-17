package com.arextest.web.model.contract.contracts;

import lombok.Data;

@Data
public class OverwriteContractRequestType {
    private String appId;

    private String operationId;

    private String operationName;
    private String operationType;
    /**
     * @See com.arextest.web.model.enums.ContractTypeEnum
     */
    private Integer contractType;

    private String operationResponse;
}
