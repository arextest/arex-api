package com.arextest.web.model.contract.contracts.filesystem;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class LeaveWorkspaceRequestType {
    @NotBlank(message = "WorkspaceId cannot be empty")
    private String workspaceId;
}
