package com.arextest.report.model.api.contracts.environment;


import lombok.Data;

@Data
public class DuplicateEnvironmentRequestType {
    private String id;
    private String workspaceId;
}
