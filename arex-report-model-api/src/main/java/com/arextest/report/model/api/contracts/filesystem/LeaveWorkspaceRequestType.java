package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

@Data
public class LeaveWorkspaceRequestType {
    private String userName;
    private String workspaceId;
}
