package com.arextest.web.model.contract.contracts.environment;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class RemoveEnvironmentRequestType {
    @NotBlank(message = "Environment id cannot be empty")
    private String id;
    private String workspaceId;
}
