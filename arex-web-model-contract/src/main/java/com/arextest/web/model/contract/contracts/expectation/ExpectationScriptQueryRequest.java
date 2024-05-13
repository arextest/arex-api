package com.arextest.web.model.contract.contracts.expectation;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExpectationScriptQueryRequest {
    @NotBlank(message = "appId can not be blank")
    private String appId;
    /**
     * operationId see Collection: ServiceOperation
     */
    private String operationId;
    /**
     * null for all
     */
    private Boolean valid;
    private Long expirationTime;
    /**
     * null for all
     * 0 for specified operation
     * 1 for all operation
     */
    private Byte scope;
}
