package com.arextest.web.model.contract.contracts.appcontract;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddDependencyToSystemRequestType {
    @NotBlank(message = "operationId cannot be blank")
    private String operationId;
    @NotBlank(message = "categoryName cannot be blank")
    private String categoryName;
    @NotBlank(message = "operationName cannot be blank")
    private String operationName;
    private String msg;
}
