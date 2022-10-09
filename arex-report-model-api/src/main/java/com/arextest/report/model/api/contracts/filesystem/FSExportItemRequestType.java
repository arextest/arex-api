package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

@Data
public class FSExportItemRequestType {
    private String workspaceId;
    private String[] path;
    private int type;
}
