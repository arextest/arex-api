package com.arextest.report.model.api.contracts.environment;


import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DuplicateEnvironmentRequestType {
    @NotBlank(message = "Environment id cannot be empty")
    private String id;
    @NotBlank(message = "Workspace id cannot be empty")
    private String workspaceId;
}
