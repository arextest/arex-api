package com.arextest.web.model.contract.contracts.filesystem;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author b_yu
 * @since 2022/10/8
 */
@Data
public class FSImportItemRequestType {
    @NotBlank(message = "WorkspaceId cannot be empty")
    private String workspaceId;
    private String[] path;
    private int type;
    @NotBlank(message = "Import string cannot be empty")
    private String importString;
}
