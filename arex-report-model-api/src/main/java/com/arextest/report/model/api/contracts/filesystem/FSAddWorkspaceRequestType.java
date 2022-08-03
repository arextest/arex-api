package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

@Data
public class FSAddWorkspaceRequestType {
    private String workspaceName;
    private String userName;
}
