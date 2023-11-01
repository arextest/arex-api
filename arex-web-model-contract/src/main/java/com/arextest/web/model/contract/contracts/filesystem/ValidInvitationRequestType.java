package com.arextest.web.model.contract.contracts.filesystem;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ValidInvitationRequestType {
    @NotBlank(message = "UserName cannot be empty")
    private String userName;
    @NotBlank(message = "WorkspaceId cannot be empty")
    private String workspaceId;
    @NotBlank(message = "Token cannot be empty")
    private String token;
}
