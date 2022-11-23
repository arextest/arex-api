package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FSExportItemRequestType {
    @NotBlank(message = "WorkspaceId cannot be empty")
    private String workspaceId;
    private String[] path;
    private int type;
}
