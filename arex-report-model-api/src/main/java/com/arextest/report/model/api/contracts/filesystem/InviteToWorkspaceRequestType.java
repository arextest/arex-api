package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

@Data
public class InviteToWorkspaceRequestType {
    private String invitor;
    private String email;
    private String workspaceId;
    private Integer role;
}
