package com.arextest.web.model.contract.contracts.filesystem;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author b_yu
 * @since 2023/1/16
 */
@Data
public class RemoveUserFromWorkspaceType {
    @NotBlank(message = "UserName cannot be empty")
    private String userName;
    @NotBlank(message = "Workspace Id cannot be empty")
    private String workspaceId;
}
