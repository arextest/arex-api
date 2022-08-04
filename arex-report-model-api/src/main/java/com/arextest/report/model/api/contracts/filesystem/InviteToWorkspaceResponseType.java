package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

import java.util.Set;

@Data
public class InviteToWorkspaceResponseType {
    private Set<String> successUsers;
    private Set<String> failedUsers;
}
