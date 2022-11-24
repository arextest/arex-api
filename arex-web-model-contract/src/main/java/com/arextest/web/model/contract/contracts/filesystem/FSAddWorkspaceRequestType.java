package com.arextest.web.model.contract.contracts.filesystem;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FSAddWorkspaceRequestType {
    private String workspaceName;
    @NotBlank(message = "UserName cannot be empty")
    private String userName;
}
