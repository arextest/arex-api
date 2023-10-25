package com.arextest.web.model.contract.contracts.environment;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class DuplicateEnvironmentRequestType {
    @NotBlank(message = "Environment id cannot be empty")
    private String id;
    @NotBlank(message = "Workspace id cannot be empty")
    private String workspaceId;
}
