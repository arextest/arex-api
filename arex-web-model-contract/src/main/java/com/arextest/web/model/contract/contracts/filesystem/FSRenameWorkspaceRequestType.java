package com.arextest.web.model.contract.contracts.filesystem;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FSRenameWorkspaceRequestType {
    @NotBlank(message = "WorkspaceId cannot empty")
    private String id;
    @NotBlank(message = "New workspace name cannot be empty")
    private String workspaceName;
}
