package com.arextest.web.model.contract.contracts.filesystem;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @author b_yu
 * @since 2023/1/16
 */
@Data
public class ChangeRoleRequestType {
    @NotBlank(message = "Workspace Id cannot be empty")
    private String workspaceId;
    @NotBlank(message = "UserName cannot be empty")
    private String userName;
    @Min(value = 1, message = "Role type must be greater than 0")
    private int role;
}
