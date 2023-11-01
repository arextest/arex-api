package com.arextest.web.model.contract.contracts.environment;

import java.util.List;
import lombok.Data;

@Data
public class QueryEnvsByWorkspaceResponseType {

  List<EnvironmentType> environments;
}
