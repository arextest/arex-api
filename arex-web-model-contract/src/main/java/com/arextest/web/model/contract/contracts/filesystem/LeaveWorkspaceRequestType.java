package com.arextest.web.model.contract.contracts.filesystem;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LeaveWorkspaceRequestType {
    @NotBlank(message = "WorkspaceId cannot be empty")
    private String workspaceId;
}
