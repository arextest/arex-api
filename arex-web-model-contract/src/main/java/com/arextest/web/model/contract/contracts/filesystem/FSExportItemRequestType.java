package com.arextest.web.model.contract.contracts.filesystem;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FSExportItemRequestType {
    @NotBlank(message = "WorkspaceId cannot be empty")
    private String workspaceId;
    private String[] path;
    /**
     * @see com.arextest.web.model.enums.ImportExportType
     */
    private int type;
}
