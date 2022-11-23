package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ValidInvitationRequestType {
    @NotBlank(message = "UserName cannot be empty")
    private String userName;
    @NotBlank(message = "WorkspaceId cannot be empty")
    private String workspaceId;
    @NotBlank(message = "Token cannot be empty")
    private String token;
}
