package com.arextest.report.model.api.contracts.environment;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RemoveEnvironmentRequestType {
    @NotBlank(message = "Environment id cannot be empty")
    private String id;
    private String workspaceId;
}
