package com.arextest.report.model.api.contracts.environment;


import lombok.Data;

@Data
public class DuplicateWorkspaceRequestType {
    private String id;
    private String workspaceId;
}
