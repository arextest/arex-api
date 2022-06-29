package com.arextest.report.model.api.contracts.environment;

import lombok.Data;

import java.util.List;

@Data
public class QueryEnvsByWorkspaceResponseType {
    List<EnvironmentType> environments;
}
