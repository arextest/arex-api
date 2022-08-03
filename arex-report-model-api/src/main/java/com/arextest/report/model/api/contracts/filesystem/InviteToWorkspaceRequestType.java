package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

import java.util.Set;

@Data
public class InviteToWorkspaceRequestType {
    private String invitor;
    private Set<String> userNames;
    private String workspaceId;
    private Integer role;
}
