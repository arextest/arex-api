package com.arextest.web.model.contract.contracts.filesystem;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FSDeleteWorkspaceRequestType {
    private String userName;
    @NotBlank(message = "Workspace id cannot be empty")
    private String workspaceId;
}
