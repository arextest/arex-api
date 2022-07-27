package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

@Data
public class LeaveWorkspaceRequestType {
    private String email;
    private String workspaceId;
}
