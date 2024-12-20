package com.arextest.web.model.contract.contracts.environment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QueryEnvsByWorkspaceRequestType {

  @NotBlank(message = "Workspace id cannot be empty")
  private String workspaceId;
}
