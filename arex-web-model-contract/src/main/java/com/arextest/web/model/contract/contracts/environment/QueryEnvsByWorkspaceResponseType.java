package com.arextest.web.model.contract.contracts.environment;

import lombok.Data;

import java.util.List;

@Data
public class QueryEnvsByWorkspaceResponseType {
    List<EnvironmentType> environments;
}
